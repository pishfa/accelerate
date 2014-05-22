/**
 * 
 */
package co.pishfa.accelerate.service;

import javax.enterprise.context.ApplicationScoped;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @author Taha Ghasemi <taha.ghasemi@gmail.com>
 * 
 */
@WebService
@ApplicationScoped
@Path("/")
public class RefService {

	public String hello(String name) {
		return "Hello " + name;
	}

	@GET
	@Produces("application/json")
	@Path("/list/{id}")
	@WebMethod(exclude = true)
	public String getList(@PathParam("id") Long id) {
		return "Hello " + id;
	}
}
