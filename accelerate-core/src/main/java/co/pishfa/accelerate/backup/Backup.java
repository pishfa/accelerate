package co.pishfa.accelerate.backup;

import co.pishfa.accelerate.storage.model.File;
import co.pishfa.security.entity.authorization.BaseSecuredEntity;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Entity
@Table(name = "ac_backup")
public class Backup extends BaseSecuredEntity {

    public enum BackupStatus {
        COMPLETED("state.completed"), IN_PROGRESS("state.in_progress"), FAILED("state.failed");

        private String name;

        BackupStatus(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate = new Date();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = true)
    private File file;

    @Enumerated(EnumType.ORDINAL)
    private BackupStatus status;

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public BackupStatus getStatus() {
        return status;
    }

    public void setStatus(BackupStatus status) {
        this.status = status;
    }

}
