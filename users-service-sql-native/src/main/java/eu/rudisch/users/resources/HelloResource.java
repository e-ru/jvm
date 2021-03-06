package eu.rudisch.users.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
public class HelloResource {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response hello() {
		return Response.ok("{\"text\": \"hello world\"}").build();
	}

}
