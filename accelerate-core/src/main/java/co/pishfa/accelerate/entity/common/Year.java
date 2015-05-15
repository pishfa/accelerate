/**
 * 
 */
package co.pishfa.accelerate.entity.common;

import javax.persistence.Entity;
import javax.persistence.Table;

import co.pishfa.accelerate.initializer.model.InitKey;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_year")
public class Year extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@InitKey
	private int number;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
