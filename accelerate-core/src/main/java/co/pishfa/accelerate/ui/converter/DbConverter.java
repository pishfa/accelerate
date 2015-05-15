package co.pishfa.accelerate.ui.converter;

import co.pishfa.accelerate.meta.domain.EntityMetadataService;
import co.pishfa.accelerate.entity.common.Entity;
import co.pishfa.accelerate.persistence.repository.EntityRepository;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * 
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
public class DbConverter implements Converter {

	private final EntityRepository<? extends Entity<Long>,Long> repository;

	public DbConverter(Class<? extends Entity<Long>> entityClass) {
		repository = EntityMetadataService.getInstance().getEntityMetadata(entityClass,Long.class).getRepository();
	}

	public DbConverter(EntityRepository<? extends Entity<Long>,Long> repository) {
		this.repository = repository;
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		if (value == null)
			return null;
		return repository.findById(Long.parseLong(value));
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null)
			return null;
		return String.valueOf(((Entity) value).getId());
	}

}
