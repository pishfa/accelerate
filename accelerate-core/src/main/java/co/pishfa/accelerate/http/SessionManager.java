package co.pishfa.accelerate.http;

import org.omnifaces.config.WebXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Note: events are not available here
 * 
 * @author Taha Ghasemi
 * 
 */
@WebListener
public class SessionManager implements HttpSessionListener, ServletContextListener {

	private static final Logger log = LoggerFactory.getLogger(SessionManager.class);

	private static ConcurrentHashMap<String, HttpSession> sessions = new ConcurrentHashMap<String, HttpSession>();

	@Override
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.put(session.getId(), session);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		sessions.remove(session.getId());
	}

	public static ConcurrentHashMap<String, HttpSession> getSessions() {
		return sessions;
	}

	public static HttpSession getSession(String id) {
		return sessions.get(id);
	}

	public static void invalidateSession(String id) {
		HttpSession s = getSession(id);
		s.invalidate();
	}

	/**
     * Note: copied from seam source
	 * Calculate the size of an HttpSession using serialization.
	 * <p>
	 * This is extremely crude and a guesstimate, especially because this ignores any serialization errors.
	 * </p>
	 * 
	 * @param id
	 *            the identifier of th HttpSession
	 * @return size in bytes
	 */
	public static long getSessionSize(String id) {
		HttpSession session = getSession(id);
		long sessionSize = 0;
		if (session != null) {
			Enumeration<?> elem = session.getAttributeNames();
			while (elem.hasMoreElements()) {
				String attName = (String) elem.nextElement();
				ByteArrayOutputStream bos = null;
				try {
					bos = new ByteArrayOutputStream();
					ObjectOutputStream out = new ObjectOutputStream(bos);
					out.writeObject(session.getAttribute(attName));
					out.close();
				} catch (Exception ex) {
					// Just swallow that
					log.warn("error during serialization, ignoring: " + ex);
				}
				if (bos != null) {
					byte[] buf = bos.toByteArray();
					sessionSize = sessionSize + buf.length;
				}
			}
		}
		return sessionSize;
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		WebXml.INSTANCE.init(event.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

}
