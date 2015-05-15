package co.pishfa.accelerate.ui.controller;

import co.pishfa.accelerate.persistence.filter.TypedFilterMetadata;
import co.pishfa.accelerate.report.Report;
import co.pishfa.accelerate.report.ReportService;
import co.pishfa.accelerate.storage.model.File;
import co.pishfa.accelerate.ui.UiAction;

import javax.inject.Inject;
import java.net.URLEncoder;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public abstract class ReportController extends PageController {

    @Inject
    private ReportService reportService;

    private TypedFilterMetadata<?> filterMetadata;

    @Override
    protected void init() {
        super.init();
        filterMetadata = new TypedFilterMetadata(getClass());
    }

    public abstract Report getReport();

    @UiAction
    public String load() {
        return null;
    }

    public boolean isClean() {
        try {
            return filterMetadata.isClean(this);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            getLogger().error("", e);
        }
        return false;
    }

    @UiAction
    public String reset() {
        try {
            filterMetadata.clean(this);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            getLogger().error("", e);
        }
        return null;
    }

    public String getViewUrl() {
        StringBuilder res = new StringBuilder(reportService.getViewUrl(getReport()));
        appendParameters(res);
        return res.toString();
    }

    public String getOutputUrl(String format) {
        StringBuilder res = new StringBuilder(reportService.getOutputUrl(getReport(), format));
        appendParameters(res);
        return res.toString();
    }

    private void appendParameters(StringBuilder url) {
        for(TypedFilterMetadata.TypedFilterFieldMetadata field : filterMetadata.getFilterFields().values()) {
            try {
                Object value = field.getField().get(this);
                if(value != null) {
                    String encoded = URLEncoder.encode(value.toString(), "UTF-8");
                    url.append("&").append(field.getPath()).append("=").append(encoded);
                }
            } catch (Exception e) {
                getLogger().error("", e);
            }
        }
    }


    public Report findByName(String name) {
        Report r = new Report();
        File file = new File();
        file.setName(name);
        r.setFile(file);
        return r;
    }
}

