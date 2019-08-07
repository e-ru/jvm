package eu.rudisch.authorization.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorization.admin.model.Role;
import eu.rudisch.authorization.admin.model.User;
import eu.rudisch.authorization.admin.repository.RoleRepository;
import eu.rudisch.authorization.admin.repository.UserRepository;
import eu.rudisch.authorization.admin.web.controller.resource.UserRestRep;

@ExtendWith(SpringExtension.class)
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

	@BeforeEach
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
