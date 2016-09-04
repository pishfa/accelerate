package co.pishfa.accelerate.storage.model;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import co.pishfa.accelerate.common.FrameworkConstants;

/**
 * This class represents multi-lingual files.
 * 
 * @author Taha Ghasemi
 * 
 */
@Embeddable
public class MultiFile {

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private File fa = new File();

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private File en = new File();

	public MultiFile() {
		// TODO Auto-generated constructor stub
	}

	public MultiFile(File en, File fa) {
		super();
		this.en = en;
		this.fa = fa;
	}

	public File getFa() {
		return fa;
	}

	public void setFa(File fa) {
		this.fa = fa;
	}

	public File getEn() {
		return en;
	}

	public void setEn(File en) {
		this.en = en;
	}

	/**
	 * retrieves the value of this multiFile according to the language
	 */
	public File getValue(String language) {
		if (FrameworkConstants.FA_LANG.equals(language))
			return fa;
		else
			return en;
	}

	/**
	 * retrieves the value of this multiFile according to the language
	 */
	public File getValue(boolean isFa) {
		if (isFa)
			return fa;
		else
			return en;
	}

	@Override
	public MultiFile clone() {
		return new MultiFile(en, fa);
	}

}
