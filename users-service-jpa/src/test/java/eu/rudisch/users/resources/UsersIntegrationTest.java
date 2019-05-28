package eu.rudisch.users.resources;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import eu.rudisch.users.TestUtils;
import eu.rudisch.users.persistance.SqlService;
import eu.rudisch.users.persistance.model.UserDetail;
import eu.rudisch.users.rest.model.User;
import eu.rudisch.users.rest.resources.Users;

class UsersIntegrationTest extends JerseyTest {

	@Mock
	SqlService sqlService;

	@Override
	public ResourceConfig configure() {
		MockitoAnnotations.initMocks(this);
		return new ResourceConfig()
				.register(Users.class)
				.register(new AbstractBinder() {
					@Override
					protected void configure() {
						bind(sqlService).to(SqlService.class);
					}
				});
	}

	@BeforeEach
	void initEach() {
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
	void shouldGetAllUsers() {
		List<UserDetail> userDetails = new ArrayList<>();
		userDetails.add(TestUtils.creatUserDetail("bos", "bob@smith.com", TestUtils.getAccount("internal"),
				TestUtils.getRole("user")));
		userDetails.add(TestUtils.creatUserDetail("bos2", "bob@smith.com", TestUtils.getAccount("internal"),
				TestUtils.getRole("user")));

		when(sqlService.getUserDetails()).thenReturn(userDetails);

		Response response = target("/users").request().get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus(), "Http Response should be 200: ");
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE),
				"Http Content-Type should be: ");
		List<User> users = response.readEntity(new GenericType<List<User>>() {
		});
		assertEquals(2, users.size());
	}

	@Test
	void shouldDeleteOneUser() {
		Response response = target("/users/1").request().delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus(), "Http Response should be 204: ");
		verify(sqlService, times(1)).removeById(1);
	}

//	@Test
	void test() {
		Response response = target("/users").request().get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus(), "Http Response should be 200: ");
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE),
				"Http Content-Type should be: ");

		String content = response.readEntity(String.class);
		assertEquals("Content of ressponse is: ", "hi", content);
	}

}
