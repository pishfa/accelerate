package co.pishfa.accelerate.report;

import javax.validation.constraints.NotNull;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public interface ReportService {

    public String getViewUrl(@NotNull Report report);
    public String getOutputUrl(@NotNull Report report, @NotNull String format);
}
