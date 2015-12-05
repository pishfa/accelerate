package co.pishfa.security.service;

import co.pishfa.accelerate.entity.common.BaseEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.security.entity.authentication.User;
import co.pishfa.security.entity.authorization.Action;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Taha Ghasemi.
 */
@Entity
@Table(name = "ac_audit_config_notif")
public class AuditNotificationConfig extends BaseEntity {
    @ManyToOne
    @InitProperty("@parent")
    AuditConfig config;

    String notifier;

    boolean enabled;

    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private List<Action> includes = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.MERGE,CascadeType.PERSIST})
    private List<User> targets = new ArrayList<>();

    public AuditConfig getConfig() {
        return config;
    }

    public void setConfig(AuditConfig config) {
        this.config = config;
    }

    public String getNotifier() {
        return notifier;
    }

    public void setNotifier(String notifier) {
        this.notifier = notifier;
    }

    public List<Action> getIncludes() {
        return includes;
    }

    public void setIncludes(List<Action> inclueds) {
        this.includes = inclueds;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<User> getTargets() {
        return targets;
    }

    public void setTargets(List<User> targets) {
        this.targets = targets;
    }
}
