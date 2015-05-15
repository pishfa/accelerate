package co.pishfa.accelerate.storage.service;

import co.pishfa.accelerate.storage.model.File;
import co.pishfa.accelerate.storage.persistence.FileRepo;
import co.pishfa.accelerate.utility.StrUtils;
import co.pishfa.security.entity.audit.AuditLevel;
import co.pishfa.security.service.AuditService;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * TODO Copied from Seam sources
 * 
 * @author Taha Ghasemi
 * 
 */
public class DownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// private static final Logger log = LoggerFactory.getLogger(DownloadServlet.class);

	private byte[] fileNotFoundImage;

	public DownloadServlet() {
		/*InputStream in = getClass().getResourceAsStream("/res/img/filenotfound.png");
		if (in != null) {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			try {
				int read = in.read(buffer);
				while (read != -1) {
					out.write(buffer, 0, read);
					read = in.read(buffer);
				}

				fileNotFoundImage = out.toByteArray();
			} catch (IOException e) {
			}
		}*/

	}

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
			IOException {
		doWork(request, response);
	}

	// TODO: All data access in this method runs with auto-commit mode, see http://jira.jboss.com/jira/browse/JBSEAM-957
	protected void doWork(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {

		/**
		 * We use file id, instead of filename to prevent attacker from accessing arbitrary files.
		 */
		File file = null;
		String id = request.getParameter("fileId");
		try {
			Long fileId = Long.valueOf(id);
			file = FileRepo.getInstance().findById(fileId);
		} catch (NumberFormatException ex) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File with id " + id);
			return;
		}

		String contentType = null;
		byte[] data = null;

		if (file != null) {
			try {
				java.io.File pFile = DefaultFileService.getInstance().download(file);
				// Render file regularly
				contentType = StrUtils.defaultIfEmpty(file.getContentType(), "application/octet-stream");
				data = FileUtils.readFileToByteArray(pFile);
			} catch (co.pishfa.security.exception.AuthorizationException e) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				AuditService.getInstance().audit(file, "unathorized.access", null, AuditLevel.RISK);
				return;
			}
		} else if (fileNotFoundImage != null) {
			contentType = "image/png";
			data = fileNotFoundImage;
		}

		if (data != null) {
			response.setContentType(contentType);
			response.setContentLength(data.length);
			// If it's not a picture or if it's a picture that is an attachment, tell the browser to download
			// the file instead of displaying it
			// TODO: What about PDFs? Lot's of people want to show PDFs inline...
			if (file != null) {
				// to avoid XSS, don't let file is open, just download it
				response.setHeader("Content-Disposition", "attachement; filename=\"" + file.getName() + "\"");
				response.setHeader("X-Content-Type-Options", "nosniff");
				response.setHeader("X-Download-Options", "noopen");
			}
			response.getOutputStream().write(data);
		}

		response.getOutputStream().flush();
	}

}
