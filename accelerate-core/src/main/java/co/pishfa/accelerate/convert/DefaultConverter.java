/**
 * 
 */
package co.pishfa.accelerate.convert;

import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.meta.domain.EntityMetadataService;
import co.pishfa.accelerate.meta.entity.EntityMetadata;
import co.pishfa.accelerate.utility.CommonUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;

/**
 * A converter that performs the required type conversations using {@link ConvertUtils}.
 * It also tries to convert from entity ids to the entity instances.
 * 
 * @author Taha Ghasemi
 * 
 */
public class DefaultConverter extends AbstractConverter {

	private static final long serialVersionUID = 1L;

	private final ConvertUtilsBean converter;

	public DefaultConverter() {
		converter = new ConvertUtilsBean();
		converter.register(true, true, 0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> T toObject(Object value, Class<T> type) {
        if(value == null)
            return null;

		if (type.isEnum()) {
            return (T) toEnum(value, (Class) type);
        } else if(Entity.class.isAssignableFrom(type)) {
            EntityMetadata metadata = EntityMetadataService.getInstance().getEntityMetadata((Class) type, Object.class);
            if(metadata != null && value.getClass().isAssignableFrom(metadata.getKeyClass())) {
                return CommonUtils.cast(metadata.getRepository().findById(converter.convert(value, metadata.getKeyClass())));
            }
		}
        //It seems we can't use type.case since it dose not cast int to Integer and so on.
        return CommonUtils.cast(converter.convert(value, type));
	}

}
