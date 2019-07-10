package eu.rudisch.authorizationadmin.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit4.SpringRunner;

import eu.rudisch.authorizationadmin.model.Role;
import eu.rudisch.authorizationadmin.model.User;
import eu.rudisch.authorizationadmin.repository.RoleRepository;
import eu.rudisch.authorizationadmin.repository.UserRepository;
import eu.rudisch.authorizationadmin.web.controller.resource.UserRestRep;

@RunWith(SpringRunner.class)
public class UserServiceImplIntegrationTest {

	@TestConfiguration
	static class UserServiceImplTestContextConfiguration {
		@Bean
		public UserService userService() {
			return new UserServiceImpl();
		}
	}

	@Autowired
	private UserService userService;

	@MockBean
	private UserRepository userRepository;

	@MockBean
	private RoleRepository roleRepository;

	@Before
	public void setUp() {
		Role role1 = new Role();
		role1.setName("test_role_one");
		Role role2 = new Role();
		role2.setName(RoleRepository.ROLE_AUTH_ADMIN);

		User user = new User();
		user.setId(0);
		user.setUsername("test_user");
		user.setRoles(List.of(role1, role2));

		Mockito.when(userRepository.findAll())
				.thenReturn(List.of(user));
		Mockito.when(userRepository.getOne(user.getId()))
				.thenReturn(user);
		Mockito.when(roleRepository.findByNames(List.of("test_role_one")))
				.thenReturn(List.of(role1));
		Mockito.when(roleRepository.findByNames(List.of("test_role_one", RoleRepository.ROLE_AUTH_ADMIN)))
				.thenReturn(List.of(role1, role2));
	}

	@Order(1)
	@Test
	public void shouldFindAllUsers() {
		var userRepresentations = userService.getUserRepresentations();
		assertEquals(1, userRepresentations.size());
	}

	@Order(2)
	@Test
	public void shouldUpdateSelfUserWithIdOneAndIsNotAbleToChangeSelfProtectedProperties() {
		UserRestRep rep = new UserRestRep();
		rep.setId(0);
		rep.setUsername("to update");
		rep.setRoleNames(List.of(RoleRepository.ROLE_AUTH_ADMIN));

		var updated = userService.updateUser(rep.getId(), rep, "test_user");
		assertEquals(updated.getUsername(), "test_user");
		assertEquals(updated.getRoleNames().size(), rep.getRoleNames().size());
	}

	@Order(3)
	@Test
	public void shouldUpdateDifferentUserWithIdOneAndIsAbleToChangeSelfProtectedProperties() {
		UserRestRep rep = new UserRestRep();
		rep.setId(0);
		rep.setUsername("to update");
		rep.setRoleNames(List.of(RoleRepository.ROLE_AUTH_ADMIN));

		var updated = userService.updateUser(rep.getId(), rep, "to update");
		assertEquals(updated.getUsername(), rep.getUsername());
		assertEquals(updated.getRoleNames().size(), rep.getRoleNames().size());
	}

	@Order(4)
	@Test
	public void shouldUpdateSelfUserWithIdOneAndNotRemoveAuthAdminRole() {
		UserRestRep rep = new UserRestRep();
		rep.setId(0);
		rep.setUsername("to update");
		rep.setRoleNames(List.of("test_role_one"));

		var updated = userService.updateUser(rep.getId(), rep, "to update");
		assertEquals(updated.getUsername(), rep.getUsername());
		assertEquals(updated.getRoleNames().size(), 2);
	}
}
