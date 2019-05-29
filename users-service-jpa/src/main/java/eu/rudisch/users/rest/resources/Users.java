package eu.rudisch.users.rest.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import eu.rudisch.users.business.ValidationHandler;
import eu.rudisch.users.persistance.SqlService;
import eu.rudisch.users.persistance.model.UserDetail;
import eu.rudisch.users.rest.model.Error;
import eu.rudisch.users.rest.model.User;

@Path("/users")
public class Users {

	@Inject
	private SqlService sqlService;

	@Inject
	private ValidationHandler validationHandler;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postUser(User user) {
		if (!validationHandler.validateAccounts(sqlService.getAccounts(), user.getAccounts())
				|| !validationHandler.validateRoles(sqlService.getRoles(), user.getRoles()))
			return Response.status(Status.BAD_REQUEST)
					.entity(Error.fromParameter("Error occurd", validationHandler.getInvalidAccounts(),
							validationHandler.getInvalidRoles()))
					.build();
		UserDetail userDetail = UserDetail.fromUser(user);
		return Response.created(URI.create("/users/" + sqlService.insertUserDetail(userDetail))).build();
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers() {
		List<User> users = sqlService.getUserDetails().stream()
				.map(userDetail -> User.fromUserDetail(userDetail))
				.collect(Collectors.toList());
		GenericEntity<List<User>> list = new GenericEntity<List<User>>(users) {
		};
		return Response.ok(list).build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserById(@PathParam("id") int id) {
		UserDetail userDetail = sqlService.getUserDetailById(id);
		if (userDetail == null)
			return Response.status(Status.BAD_REQUEST).build();
		return Response.ok(User.fromUserDetail(userDetail)).build();
	}

//	@GET
//	@Path("/{username}")
//	@Produces(MediaType.APPLICATION_JSON)
//	public Response getUserByUserName(@PathParam("username") String userName) {
//		return Response.ok("{\"text\": \"hello world\"}").build();
//	}

	@PUT
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response putUserById(@PathParam("id") int id, User user) {
		UserDetail updated = sqlService.updateUserDetailById(id, UserDetail.fromUser(user));
		return Response.ok(User.fromUserDetail(updated)).build();
	}

	@PATCH
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response patchUserById(@PathParam("id") int id) {
		return null;
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response deleteUserById(@PathParam("id") int id) {
		sqlService.removeById(id);
		return Response.noContent().build();
	}

}
