package co.pishfa.accelerate.report;

import co.pishfa.accelerate.storage.model.File;
import co.pishfa.security.entity.authorization.BaseSecuredEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Entity
public class Report extends BaseSecuredEntity {

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    private File file;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
