/**
 * 
 */
package co.pishfa.accelerate.clone;

import java.lang.reflect.Field;
import java.util.Map;

import co.pishfa.accelerate.cdi.CdiUtils;
import co.pishfa.accelerate.storage.service.FileService;
import co.pishfa.accelerate.storage.model.File;

/**
 * @author Taha Ghasemi
 * 
 */
public class FileClonner implements CustomClonner {

	@Override
	public Object clone(Cloner cloner, Map<Object, Object> clones, Field field, Object object, Object fieldValue) throws Exception {
		if (fieldValue == null || !(fieldValue instanceof File)) {
			return fieldValue;
		}
		File dest = (File) cloner.cloneInternal(fieldValue, clones);
		CdiUtils.getInstance(FileService.class).copy((File) fieldValue, dest);
		return dest;
	}

}
