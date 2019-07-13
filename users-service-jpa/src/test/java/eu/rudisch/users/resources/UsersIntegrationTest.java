package eu.rudisch.users.resources;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Entity;
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
import eu.rudisch.users.business.ValidationHandler;
import eu.rudisch.users.persistance.SqlService;
import eu.rudisch.users.persistance.model.UserDetail;
import eu.rudisch.users.rest.model.AccountRep;
import eu.rudisch.users.rest.model.Error;
import eu.rudisch.users.rest.model.User;
import eu.rudisch.users.rest.resources.Users;

class UsersIntegrationTest extends JerseyTest {

	@Mock
	SqlService sqlService;

	@Mock
	ValidationHandler validationHandler;

	@Override
	public ResourceConfig configure() {
		MockitoAnnotations.initMocks(this);
		return new ResourceConfig()
				.register(Users.class)
				.register(new AbstractBinder() {
					@Override
					protected void configure() {
						bind(sqlService).to(SqlService.class);
						bind(validationHandler).to(ValidationHandler.class);
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

	@SuppressWarnings("unchecked")
	@Test
	void shouldCreateUser() {
		User user = User.fromParameter("bob", "smith", Set.of(AccountRep.fromParameter("internal")),
				Set.of("member"));

		when(validationHandler.validateAccounts(any(List.class), any(Set.class))).thenReturn(true);
		when(validationHandler.validateRoles(any(List.class), any(Set.class))).thenReturn(true);
		when(sqlService.insertUserDetail(any(UserDetail.class))).thenReturn(1);

		Response response = target("/users").request().post(Entity.entity(user, MediaType.APPLICATION_JSON));
		assertEquals(Status.CREATED.getStatusCode(), response.getStatus(), "Http Response should be 201: ");
		assertTrue(response.getLocation().toString().contains("/users/1"));
		verify(validationHandler, times(1)).validateAccounts(any(List.class), any(Set.class));
		verify(validationHandler, times(1)).validateRoles(any(List.class), any(Set.class));
		verify(sqlService, times(1)).getAccounts();
		verify(sqlService, times(1)).getRoles();
	}

	@SuppressWarnings("unchecked")
	@Test
	void shouldNotCreateUserInvalidAccount() {
		User user = User.fromParameter("bob", "smith", Set.of(AccountRep.fromParameter("wrongAccount")),
				Set.of("member"));

		when(validationHandler.validateAccounts(any(List.class), any(Set.class))).thenReturn(false);
		when(validationHandler.getInvalidAccounts()).thenReturn(new HashSet<String>(Set.of("wrongAccount")));
		when(sqlService.insertUserDetail(any(UserDetail.class))).thenReturn(1);

		Response response = target("/users").request().post(Entity.entity(user, MediaType.APPLICATION_JSON));
		Error error = response.readEntity(Error.class);

		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus(), "Http Response should be 400: ");
		assertTrue(error.getInvalidAccounts().contains("wrongAccount"));
		verify(validationHandler, times(1)).validateAccounts(any(List.class), any(Set.class));
		verify(sqlService, times(1)).getAccounts();
	}

	@SuppressWarnings("unchecked")
	@Test
	void shouldNotCreateUserInvalidRole() {
		User user = User.fromParameter("bob", "smith", Set.of(AccountRep.fromParameter("internal")),
				Set.of("wrongRole"));

		when(validationHandler.validateAccounts(any(List.class), any(Set.class))).thenReturn(true);
		when(validationHandler.validateRoles(any(List.class), any(Set.class))).thenReturn(false);
		when(validationHandler.getInvalidRoles()).thenReturn(new HashSet<String>(Set.of("wrongRole")));
		when(sqlService.insertUserDetail(any(UserDetail.class))).thenReturn(1);

		Response response = target("/users").request().post(Entity.entity(user, MediaType.APPLICATION_JSON));
		Error error = response.readEntity(Error.class);

		assertEquals(Status.BAD_REQUEST.getStatusCode(), response.getStatus(), "Http Response should be 400: ");
		assertTrue(error.getInvalidRoles().contains("wrongRole"));
		verify(validationHandler, times(1)).validateAccounts(any(List.class), any(Set.class));
		verify(validationHandler, times(1)).validateRoles(any(List.class), any(Set.class));
		verify(sqlService, times(1)).getAccounts();
		verify(sqlService, times(1)).getRoles();
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
	void shouldGetSingleUser() {
		UserDetail userDetail = TestUtils.creatUserDetail("bos", "bob@smith.com", TestUtils.getAccount("internal"),
				TestUtils.getRole("member"));

		when(sqlService.getUserDetailById(1)).thenReturn(userDetail);

		Response response = target("/users/1").request().get();
		assertEquals(Status.OK.getStatusCode(), response.getStatus(), "Http Response should be 200: ");
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE),
				"Http Content-Type should be: ");
		User respUser = response.readEntity(User.class);
		assertEquals(userDetail.getFirstName(), respUser.getFirstName());
		assertEquals(userDetail.getLastName(), respUser.getLastName());
		verify(sqlService, times(1)).getUserDetailById(1);
	}

	@Test
	void shouldReplaceEverythingDuringUpdate() {
		UserDetail userDetail = TestUtils.creatUserDetail("bos", "bob@smith.com", TestUtils.getAccount("internal"),
				TestUtils.getRole("member"));
		userDetail.setFirstName("Bob2");
		userDetail.setLastName("Smith2");
		userDetail.getMembership().setAccounts(null);
		userDetail.getMembership().setRoles(null);
		User user = User.fromParameter("Bob2", "Smith2", null, null);

		when(sqlService.updateUserDetailById(any(Integer.class), any(UserDetail.class))).thenReturn(userDetail);

		Response response = target("/users/1").request().put(Entity.entity(user, MediaType.APPLICATION_JSON));
		assertEquals(Status.OK.getStatusCode(), response.getStatus(), "Http Response should be 200: ");
		assertEquals(MediaType.APPLICATION_JSON, response.getHeaderString(HttpHeaders.CONTENT_TYPE),
				"Http Content-Type should be: ");
		User respUser = response.readEntity(User.class);
		assertEquals(user.getFirstName(), respUser.getFirstName());
		assertEquals(user.getLastName(), respUser.getLastName());
		assertNull(respUser.getAccounts());
		assertNull(respUser.getRoles());
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
