package co.pishfa.accelerate.report;

import co.pishfa.accelerate.config.Config;
import co.pishfa.accelerate.config.cdi.Global;
import co.pishfa.accelerate.service.Service;
import co.pishfa.accelerate.i18n.domain.Locale;

import javax.inject.Inject;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
@Service
public class BirtReportService implements ReportService {

    @Inject
    @Global
    private Config config;

    @Inject
    private Locale locale;

    @Override
    public String getViewUrl(Report report) {
        String server = config.getString("report.server");
        StringBuilder url = new StringBuilder(server);
        url.append("/run?__report=").append(report.getFile().getName()).append("&__format=html&__masterpage=false&__locale=")
                .append(locale.getLocale()).append("&__timezone=").append(locale.getTimeZone().getID())
                .append("&__rtl=").append(locale.isRTL());
        return url.toString();
    }

    @Override
    public String getOutputUrl(Report report, String format) {
        String server = config.getString("report.server");
        StringBuilder url = new StringBuilder(server);
        url.append("/output?__report=").append(report.getFile().getName()).append("&__masterpage=true&__dpi=120&__locale=")
                .append(locale.getLocale()).append("&__timezone=").append(locale.getTimeZone().getID())
                .append("&__rtl=").append(locale.isRTL()).append("&__format=").append(format);
        return url.toString();
    }
}
