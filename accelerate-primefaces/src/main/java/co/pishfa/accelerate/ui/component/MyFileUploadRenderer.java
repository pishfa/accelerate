package co.pishfa.accelerate.ui.component;

import org.primefaces.component.fileupload.FileUploadRenderer;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class MyFileUploadRenderer extends FileUploadRenderer {

    @Override
    public void decode(FacesContext context, UIComponent component) {
        if (context.getExternalContext().getRequestContentType().toLowerCase().startsWith("multipart/")) {
            super.decode(context, component);
        }
    }

}
