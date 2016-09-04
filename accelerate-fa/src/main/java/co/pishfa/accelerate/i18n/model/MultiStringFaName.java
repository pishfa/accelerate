package co.pishfa.accelerate.i18n.model;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import co.pishfa.accelerate.common.FrameworkConstants;
import co.pishfa.accelerate.i18n.domain.PersianUtils;

/**
 * This class represents short multi-lingual strings (less than 250 characters) which are suitable for naming purposes.
 * 
 * 
 * @author Taha Ghasemi
 * 
 */
// @Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
// @BatchSize(size=100)
@Embeddable
public class MultiStringFaName implements MultiStringFa {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Length(max = 250)
	@NotEmpty
	// @Pattern(regex = "[^@#%$!=\\.\\*\\+\\^\\&]*")
	@Column(length = 250)
	private String fa;

	@Column(length = 250)
	@Basic(fetch = FetchType.LAZY)
	private String faSort;

	@Length(max = 250)
	// @NotEmpty
	// @Pattern(regex="[^@#%$!=\\.\\*\\+\\^\\&]*")
	@Column(length = 250)
	private String en;

	public MultiStringFaName() {
		// TODO Auto-generated constructor stub
	}

	public MultiStringFaName(String en, String fa) {
		super();
		setEn(en);
		setFa(fa);
	}

	@Override
	public String getFa() {
		return fa;
	}

	@Override
	public void setFa(String fa) {
		this.fa = fa;
		preUpdateAndPersist();
	}

	@Override
	public String getEn() {
		return en;
	}

	@Override
	public void setEn(String en) {
		this.en = en;
	}

	/**
	 * retrieves the value of this multiString according to the language
	 */
	@Override
	public String getValue(String language) {
		if (FrameworkConstants.FA_LANG.equals(language)) {
			return fa;
		} else {
			return en;
		}
	}

	@Override
	public MultiStringFaName clone() {
		return new MultiStringFaName(en, fa);
	}

	public String getFaSort() {
		return faSort;
	}

	@PreUpdate
	@PrePersist
	public void preUpdateAndPersist() {
		if (fa != null) {
			fa = PersianUtils.convertToPersian(fa);
			faSort = PersianUtils.convertForSort(fa);
		}
	}

	@Override
	public void setValue(String value, String language) {
		if (FrameworkConstants.FA_LANG.equals(language)) {
			setFa(value);
		} else {
			setEn(value);
		}
	}

}
