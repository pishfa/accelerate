package co.pishfa.security.service;

import co.pishfa.accelerate.config.entity.PersistentConfig;
import co.pishfa.accelerate.initializer.model.InitEntity;
import co.pishfa.accelerate.initializer.model.InitProperty;
import co.pishfa.accelerate.entity.common.BaseEntity;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

/**
 * A type-safe configuration entity. The implementation class must be annotated with {@link co.pishfa.accelerate.config.ConfigEntity}.
 *
 *
 * @author Taha Ghasemi
 */
@MappedSuperclass
@InitEntity
public class PersistentConfigEntity extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, cascade = {}, optional = false)
    @InitProperty("@parent")
    private PersistentConfig config;

    public PersistentConfig getConfig() {
        return config;
    }

    public void setConfig(PersistentConfig config) {
        this.config = config;
    }
}
