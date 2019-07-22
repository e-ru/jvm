package eu.rudisch.authorizationserver.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import eu.rudisch.authorizationserver.AuthorizationServerApplication;
import eu.rudisch.authorizationserver.model.Permission;
import eu.rudisch.authorizationserver.model.Role;
import eu.rudisch.authorizationserver.model.User;
import eu.rudisch.authorizationserver.repository.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = AuthorizationServerApplication.class)
class UserDetailServiceImplTest {

	static final String USERNAME = "test_user";
	static final String ROLE = "test_role";
	static final String PERMISSION = "test_permission";

	@TestConfiguration
	static class UserDetailServiceImplContextConfiguration {
		@Bean
		public UserDetailServiceImpl userDetailServiceImpl() {
			return Mockito.mock(UserDetailServiceImpl.class);
		}
	}

	@MockBean
	private UserRepository userRepository;

	@Autowired
	UserDetailServiceImpl userDetailServiceImpl;

	@Test
	void shouldReturnUserDetail() {
		Permission p = new Permission();
		Role r = new Role();
		User u = new User();
		p.setName(PERMISSION);
		r.setName(ROLE);
		r.setPermissions(List.of(p));
		u.setUsername(USERNAME);
		u.setRoles(List.of(r));
		u.setAccountNonExpired(true);
		u.setAccountNonLocked(true);
		u.setCredentialsNonExpired(true);
		u.setEnabled(true);

		Mockito.when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(u));

		UserDetails userDetails = userDetailServiceImpl.loadUserByUsername(USERNAME);
		Set<String> auth = userDetails.getAuthorities().stream()
				.map(ga -> ga.getAuthority())
				.collect(Collectors.toSet());

		assertTrue(auth.contains(ROLE));
		assertTrue(auth.contains(PERMISSION));
	}

}
