/**
 * 
 */
package co.pishfa.accelerate.persistence.query;

import javax.enterprise.util.Nonbinding;
import javax.interceptor.InterceptorBinding;
import javax.persistence.Query;
import java.lang.annotation.*;
import java.util.List;

/**
 * Runs a query with the given query or using a named query. The parameters are filled from the method parameters in the
 * same order. The method parameters can optionally be annotated with {@link QueryParam}, {@link QueryLikeParam},
 * {@link QueryFirstParam}, and {@link QueryMaxParam}.
 * 
 * This annotation must be only used in the descendants of {@link co.pishfa.accelerate.persistence.repository.BaseJpaRepo}. Based on the return type of
 * the method, the corresponding execution type is selected. More specifically:
 * <ul>
 * <li>
 * if the return type is assignable from {@link List}, the {@link Query#getResultList()} is called,</li>
 * <li>
 * if the return type is void, the {@link Query#executeUpdate()} is called,</li>
 * <li>
 * if the return type is boolean, the <code>(Long) query.getSingleResult() > 0</code> is called,</li>
 * <li>
 * otherwise the {@link Query#getSingleResult()} is called.</li>
 * </ul>
 * 
 * @author Taha Ghasemi
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@InterceptorBinding
public @interface QueryRunner {

	/**
	 * When expecting only one instance to be returned but nothing found, should the exception be thrown or null be
	 * returned. By the rules of {@link co.pishfa.accelerate.persistence.repository.EntityRepository} it is recommended that this property is set to false.
	 * 
	 * @return
	 */
	@Nonbinding
	boolean nullOnNoResult() default false;

	/**
	 * The query string.
	 * 
	 * @return
	 */
	@Nonbinding
	String value() default "";

	/**
	 * This can be used to specify only the where part of the query. The header of the query which is added is
	 * "select e from EntityType e where "
	 */
	@Nonbinding
	String where() default "";

	/**
	 * Specifies the name of a named query. if value and where fields are empty, this field defaults to the
	 * entity_class_name.method_name.
	 * 
	 */
	@Nonbinding
	String named() default "";

	/**
	 * Maximum results that should be returned. 0 means no restriction. Not that a method parameter that annotated with
	 * {@link QueryMaxParam} takes precedence over this.
	 */
	@Nonbinding
	int maxResults() default 0;

    @Nonbinding
    boolean nativeSql() default false;

    @Nonbinding
    boolean dynamic() default false;

}
