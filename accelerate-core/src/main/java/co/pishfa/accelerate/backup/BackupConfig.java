package co.pishfa.accelerate.backup;

import co.pishfa.accelerate.config.ConfigEntity;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@ConfigEntity("admin.backup")
public class BackupConfig {

    private String schedule;
    private String executorCommand;
    private Integer deletePeriod;
    private String storage;

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getExecutorCommand() {
        return executorCommand;
    }

    public void setExecutorCommand(String executorCommand) {
        this.executorCommand = executorCommand;
    }

    public Integer getDeletePeriod() {
        return deletePeriod;
    }

    public void setDeletePeriod(Integer deletePeriod) {
        this.deletePeriod = deletePeriod;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }
}
