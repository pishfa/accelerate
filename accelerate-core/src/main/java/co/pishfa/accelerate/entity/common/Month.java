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
@Table(name = "ac_month")
public class Month extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@InitKey
	private int number;
	private String title;

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
