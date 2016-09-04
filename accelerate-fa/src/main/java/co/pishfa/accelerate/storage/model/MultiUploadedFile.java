package co.pishfa.accelerate.storage.model;

/**
 * Represent a single file for uploading
 * 
 * @author Ghasemi
 * 
 */
public class MultiUploadedFile {
	private UploadedFile fa = new UploadedFile();
	private UploadedFile en = new UploadedFile();

	public UploadedFile getFa() {
		return fa;
	}

	public void setFa(UploadedFile fa) {
		this.fa = fa;
	}

	public UploadedFile getEn() {
		return en;
	}

	public void setEn(UploadedFile en) {
		this.en = en;
	}

	public UploadedFile getValue(boolean isFa) {
		if (isFa) {
			return fa;
		}
		return en;
	}

}
