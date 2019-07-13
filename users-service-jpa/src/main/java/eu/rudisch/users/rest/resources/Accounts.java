package eu.rudisch.users.rest.resources;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import eu.rudisch.users.persistance.SqlService;
import eu.rudisch.users.rest.model.AccountRep;
import eu.rudisch.users.security.JwtOAuth2;

@JwtOAuth2(scope = "create_oauth")
@Path("/accounts")
public class Accounts {

	@Inject
	private SqlService sqlService;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAccounts() {
		List<AccountRep> users = sqlService.getAccounts().stream()
				.map(account -> AccountRep.fromParameter(account.getName()))
				.collect(Collectors.toList());
		GenericEntity<List<AccountRep>> list = new GenericEntity<List<AccountRep>>(users) {
		};
		return Response.ok(list).build();
	}
}
