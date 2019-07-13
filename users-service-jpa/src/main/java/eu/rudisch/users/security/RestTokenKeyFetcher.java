package eu.rudisch.users.security;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

public class RestTokenKeyFetcher {
	public String getTokenKey() {
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target("http://localhost:9191")
				.path("oauth/token_key");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		TokenKey tokenKey = invocationBuilder.get(TokenKey.class);
		return tokenKey.getValue();
	}
}
