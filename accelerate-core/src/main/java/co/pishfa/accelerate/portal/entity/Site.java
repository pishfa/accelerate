/**
 * 
 */
package co.pishfa.accelerate.portal.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import co.pishfa.security.entity.authorization.BaseSecuredEntity;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_site")
public class Site extends BaseSecuredEntity {

	private static final long serialVersionUID = 1L;

	private String title;

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
