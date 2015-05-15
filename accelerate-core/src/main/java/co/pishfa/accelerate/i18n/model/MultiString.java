/**
 * 
 */
package co.pishfa.accelerate.i18n.model;

import java.io.Serializable;

/**
 * Represents an string that is stored in different languages simultaneously.
 * 
 * @author Taha Ghasemi
 * 
 */
public interface MultiString extends Serializable {

	public String getFa();

	public void setFa(String fa);

	public String getEn();

	public void setEn(String en);

	public String getValue(String language);

	public void setValue(String value, String language);

}
