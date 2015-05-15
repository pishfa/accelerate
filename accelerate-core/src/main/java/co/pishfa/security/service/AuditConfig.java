package co.pishfa.security.service;

import co.pishfa.accelerate.config.ConfigEntity;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.entity.authorization.Action;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
* @author Taha Ghasemi
*/
@ConfigEntity("security.audit")
@Entity
@Table(name = "ac_audit_config")
public class AuditConfig extends PersistentConfigEntity {

    private int deletePeriod;

    @Enumerated(EnumType.ORDINAL)
    private AuditLevel levelThreshold;

    private int flushInterval;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private List<Action> excludes = new ArrayList<>();

    public int getDeletePeriod() {
        return deletePeriod;
    }

    public void setDeletePeriod(int deletePeriod) {
        this.deletePeriod = deletePeriod;
    }

    public AuditLevel getLevelThreshold() {
        return levelThreshold;
    }

    public void setLevelThreshold(AuditLevel levelThreshold) {
        this.levelThreshold = levelThreshold;
    }

    public int getFlushInterval() {
        return flushInterval;
    }

    public void setFlushInterval(int flushInterval) {
        this.flushInterval = flushInterval;
    }

    public List<Action> getExcludes() {
        return excludes;
    }

    public void setExcludes(List<Action> excludes) {
        this.excludes = excludes;
    }

}
