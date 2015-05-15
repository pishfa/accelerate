/**
 * 
 */
package co.pishfa.accelerate.exception;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Taha Ghasemi
 * 
 */
@WebFilter("/*")
public class ExceptionHandlingFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(ExceptionHandlingFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		try {
			chain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Unhandeled exception", e);
			((HttpServletResponse) response).sendError(HttpServletResponse.SC_EXPECTATION_FAILED,
					"Could not complete the request");
		}
	}

	@Override
	public void destroy() {
	}

}
