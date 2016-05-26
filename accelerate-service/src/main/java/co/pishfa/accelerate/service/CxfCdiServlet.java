package co.pishfa.accelerate.service;

import org.apache.cxf.transport.servlet.CXFNonSpringServlet;

import javax.servlet.ServletConfig;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 */
public class CxfCdiServlet extends CXFNonSpringServlet {
    @Override
    protected void loadBus(ServletConfig sc) {
        this.bus = new AccelerateBusFactory().getBus();
    }
}
