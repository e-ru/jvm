package eu.rudisch.users.resources;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UsersIntegrationTest extends JerseyTest {

	// mock sqlService

	@Override
	protected Application configure() {
		return new ResourceConfig(Users.class);
	}

	@BeforeEach
	void init() {
		try {
			super.setUp();
		} catch (Exception e) {
			fail("Failed to setup test.");
		}
	}

	@AfterEach
	void destroy() {
		try {
			super.tearDown();
		} catch (Exception e) {
			fail("Failed to setup test.");
		}
	}

	@Test
	void test() {
		Response response = target("/users").request().get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus(), "Http Response should be 200: ");
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE),
				"Http Content-Type should be: ");

		String content = response.readEntity(String.class);
		assertEquals("Content of ressponse is: ", "hi", content);
	}

}
