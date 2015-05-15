/**
 * 
 */
package co.pishfa.accelerate.entity.common;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * @author Taha Ghasemi
 * 
 */
@Embeddable
public class Contact {

	@Basic(fetch = FetchType.LAZY, optional = false)
	private char dummy = 'a';

	@Size(max = 200)
	private String fax;

	@Size(max = 200)
	private String tel; // either direct or internal

	@Size(max = 200)
	private String mobile; // either direct or internal

	@Size(max = 5)
	/* @Email */
	private String email;

	@Size(max = 100)
	private String site;

	private String address;

	@Pattern(regexp = "^$|[0-9]{10}", message = "{invalid.postalCode}")
	@Column(length = 10)
	private String postalCode;

	private String description;

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public char getDummy() {
		return dummy;
	}

	public void setDummy(char dummy) {
		this.dummy = dummy;
	}

}
