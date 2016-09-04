package co.pishfa.accelerate.i18n.model;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.validator.constraints.Length;

import co.pishfa.accelerate.common.FrameworkConstants;
import co.pishfa.accelerate.i18n.domain.LocalizationService;
import co.pishfa.accelerate.i18n.domain.PersianUtils;

/**
 * This class represents short multi-lingual strings (less than 1000 characters).
 * 
 * @author Taha Ghasemi
 * 
 */
@Embeddable
public class MultiStringFaShort implements MultiStringFa {

	private static final long serialVersionUID = 1L;

	@Length(max = 1024)
	private String fa;

	@Length(max = 1024)
	private String en;

	// Also to avoid nulling the embedable
	@Basic(fetch = FetchType.LAZY, optional = false)
	@Length(max = 1024)
	private String faSort = "a";

	public MultiStringFaShort() {
	}

	public MultiStringFaShort(String en, String fa) {
		super();
		setEn(en);
		setFa(fa);
	}

	public MultiStringFaShort(String key) {
		LocalizationService localizationService = LocalizationService.getInstance();
		setEn(localizationService.getMessages(FrameworkConstants.EN_LOCALE).get(key));
		setFa(localizationService.getMessages(FrameworkConstants.FA_LOCALE).get(key));
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
			return getFa();
		} else {
			return getEn();
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

	@Override
	public MultiStringFaShort clone() {
		return new MultiStringFaShort(en, fa);
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

}
