/**
 *
 */
package co.pishfa.accelerate.persistence.query;

import co.pishfa.accelerate.persistence.repository.BaseJpaRepo;
import co.pishfa.accelerate.template.ExpressionInterpolator;
import co.pishfa.accelerate.utility.StrUtils;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Note that this mechanism is only useful when you want to call the annotated methods from outside (that is on the
 * proxy) not within a repository call another repository with this annotation since there is no proxy here.
 *
 * @author Taha Ghasemi
 */
@Interceptor
@QueryRunner
public class QueryRunnerInterceptor implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private ExpressionInterpolator expressionInterpolator;

    @AroundInvoke
    public Object aroundInvoke(InvocationContext ic) throws Exception {
        if (!(ic.getTarget() instanceof BaseJpaRepo)) {
            throw new IllegalArgumentException(
                    "QueryRunner annotation should be used in descendants of AbstractJpaRepository only");
        }

        Method m = ic.getMethod();
        QueryRunner qr = m.getAnnotation(QueryRunner.class);
        if (qr != null) {
            Query query = buildQuery(ic, m, qr);
            return executeQuery(m, qr, query);
        } else {
            return ic.proceed();
        }
    }

    protected Query buildQuery(InvocationContext ic, Method m, QueryRunner qr) {
        BaseJpaRepo<?, Long> repository = (BaseJpaRepo<?, Long>) ic.getTarget();

        // Construct the query string, if any
        String queryStr = null;
        if (StrUtils.isEmpty(qr.where())) {
            queryStr = qr.value();
        } else {
            StringBuilder queryStrBuilder = new StringBuilder("select e from ").append(repository.entityAlias())
                    .append(" e where ").append(qr.where());
            queryStr = queryStrBuilder.toString();
        }

        // Construct the query itself, either from query string or named.
        Query query = null;
        if (!StrUtils.isEmpty(queryStr)) {
            if(qr.dynamic()) {
                queryStr = expressionInterpolator.populate(queryStr, getNamedParams(m,ic));
            }
            if(qr.nativeSql())
                query = repository.getEntityManager().createNativeQuery(queryStr);
            else
                query = repository.getEntityManager().createQuery(queryStr);
        } else { // named query mode
            String queryName = StrUtils.defaultIfEmpty(qr.named(), repository.entityAlias() + "." + m.getName());
            query = repository.getEntityManager().createNamedQuery(queryName);
        }

        // Set the query parameters
        if (qr.maxResults() > 0) {
            query.setMaxResults(qr.maxResults());
        }
        Annotation[][] parameterAnnotations = m.getParameterAnnotations();
        int i = 0;
        for (Object param : ic.getParameters()) {
            Annotation[] annotations = parameterAnnotations[i++];
            processParam(query, i, param, annotations);
        }
        return query;
    }

    private Map<String, Object> getNamedParams(Method m, InvocationContext ic) {
        Map<String, Object> res = new HashMap<>();
        Annotation[][] parameterAnnotations = m.getParameterAnnotations();
        int i = 0;
        for (Object param : ic.getParameters()) {
            Annotation[] annotations = parameterAnnotations[i++];
            for(Annotation annotation : annotations) {
                if(annotation instanceof QueryParam) {
                    QueryParam queryParam = (QueryParam) annotation;
                    if(StrUtils.isEmpty(queryParam.name())) {
                        res.put("param" + i, param);
                    } else {
                        res.put(queryParam.name(), param);
                    }
                }
            }
        }
        return res;
    }

    private void processParam(Query query, int paramPos, Object param, Annotation[] annotations) {
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i] instanceof QueryMaxParam) {
                query.setMaxResults((int) param);
                return;
            }
            if (annotations[i] instanceof QueryFirstParam) {
                query.setFirstResult((int) param);
                return;
            }
            if (annotations[i] instanceof QueryLikeParam) {
                StringBuilder finalParam = new StringBuilder();
                QueryLikeParam queryLikeParam = (QueryLikeParam) annotations[i];
                if (queryLikeParam.begin()) {
                    finalParam.append('%');
                }
                finalParam.append(param);
                if (queryLikeParam.end()) {
                    finalParam.append('%');
                }
                if (StrUtils.isEmpty(queryLikeParam.name()))
                    query.setParameter(paramPos, finalParam.toString());
                else
                    query.setParameter(queryLikeParam.name(), finalParam.toString());
                return;
            }
            if (annotations[i] instanceof QueryParam) {
                QueryParam queryParam = (QueryParam) annotations[i];
                if(!queryParam.ignore() && (!queryParam.optional() || param != null))
                    query.setParameter(queryParam.name(), param);
                return;
            }
        }
        query.setParameter(paramPos, param);
    }

    protected Object executeQuery(Method m, QueryRunner qr, Query query) {
        Class<?> returnType = m.getReturnType();
        if (returnType == null || returnType == Void.TYPE || returnType == void.class) {
            query.executeUpdate();
            return null;
        } else if (List.class.isAssignableFrom(returnType)) {
            return query.getResultList();
        } else if (returnType == Boolean.TYPE || returnType == Boolean.class) {
            Object result = query.getSingleResult();
            if(result == null)
                return returnType == Boolean.TYPE? false : null;
            if (result instanceof Long)
                return (Long) result > 0;
            else if (result instanceof Integer)
                return (Integer) result > 0;
            return result;
        } else if (returnType == Long.TYPE || returnType == Long.class) {
            Number numRes = (Number) query.getSingleResult();
            if(numRes == null)
                return returnType == Long.TYPE? 0L : null;
            return numRes.longValue();
        } else if (returnType == Integer.TYPE || returnType == Integer.class) {
            Number numRes = (Number) query.getSingleResult();
            if(numRes == null)
                return returnType == Integer.TYPE? 0 : null;
            return numRes.intValue();
        } else if (returnType == Float.TYPE || returnType == Float.class) {
            Number numRes = (Number) query.getSingleResult();
            if(numRes == null)
                return returnType == Float.TYPE? 0f : null;
            return numRes.floatValue();
        } else if (returnType == Double.TYPE || returnType == Double.class) {
            Number numRes = (Number) query.getSingleResult();
            if(numRes == null)
                return returnType == Double.TYPE? 0d : null;
            return numRes.doubleValue();
        } else {
            if (!qr.nullOnNoResult()) {
                return query.getSingleResult();
            }
            try {
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        }
    }

}
