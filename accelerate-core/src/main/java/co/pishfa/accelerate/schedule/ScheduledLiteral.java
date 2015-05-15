package co.pishfa.accelerate.schedule;

import java.lang.annotation.Annotation;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class ScheduledLiteral implements Scheduled {

    private String value;

    public ScheduledLiteral(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return Scheduled.class;
    }
}
