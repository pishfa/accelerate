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
public interface MultiStringFa extends Serializable {

	String getFa();

	void setFa(String fa);

	String getEn();

	void setEn(String en);

	String getValue(String language);

	void setValue(String value, String language);

}
