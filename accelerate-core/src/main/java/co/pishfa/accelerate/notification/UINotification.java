package co.pishfa.accelerate.notification;

import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.security.entity.authentication.User;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Entity
@Table(name = "ac_ui_notification")
public class UINotification extends BaseEntity {

    private String title;
    private String message;
    @Column(name = "from_col")
    private String from;
    @ManyToOne
    private User to;
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationTime;
    private boolean read;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public User getTo() {
        return to;
    }

    public void setTo(User to) {
        this.to = to;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
