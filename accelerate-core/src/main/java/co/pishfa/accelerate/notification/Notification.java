package co.pishfa.accelerate.notification;

import co.pishfa.security.entity.authentication.User;

import java.util.Date;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class Notification {

    private String title;
    private String message;
    private Object[] parameters;
    private String from;
    private User to;
    private Date creationTime;

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

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
