/**
 * 
 */
package co.pishfa.accelerate.entity.common;

import co.pishfa.accelerate.initializer.model.InitKey;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_day")
public class Day extends BaseEntity {

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
