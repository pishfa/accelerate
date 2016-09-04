package co.pishfa.accelerate.i18n.model;

import co.pishfa.accelerate.common.FrameworkConstants;
import co.pishfa.accelerate.i18n.domain.PersianUtils;
import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.SecurityUtils;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

/**
 * This class represents long multi-lingual strings (more than 1000 characters).
 * 
 * @author Taha Ghasemi
 * 
 */
@Entity
@Table(name = "ac_multi_string_long")
// @Embeddable
public class MultiStringFaLong extends BaseEntity implements MultiStringFa {

	private static final long serialVersionUID = 1L;

	@Length(max = 12000)
	// @Basic(fetch=FetchType.LAZY)
	private String fa;

	@Length(max = 12000)
	// @Basic(fetch=FetchType.LAZY)
	private String en;

	public MultiStringFaLong() {
	}

	public MultiStringFaLong(String en, String fa) {
		super();
		this.en = en;
		this.fa = fa;
	}

	@Override
	public String getFa() {
		return fa;
	}

	@Override
	public void setFa(String fa) {
		this.fa = fa;
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
	public MultiStringFaLong clone() {
		return new MultiStringFaLong(en, fa);
	}

	@PreUpdate
	@PrePersist
	public void preUpdateAndPersist() {
		if (fa != null) {
			fa = SecurityUtils.removeXSS(fa, true);
			fa = PersianUtils.convertToPersian(fa);
		}
		if (en != null) {
			en = SecurityUtils.removeXSS(en, true);
		}
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return StrUtils.isEmpty(fa) && StrUtils.isEmpty(en);
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
