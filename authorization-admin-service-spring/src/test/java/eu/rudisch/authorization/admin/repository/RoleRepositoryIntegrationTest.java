package eu.rudisch.authorization.admin.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import eu.rudisch.authorization.admin.model.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class RoleRepositoryIntegrationTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private RoleRepository roleRepository;

	@Test
	public void shouldFindOneRole() {
		Role role = new Role();
		role.setName("test_role");
		entityManager.persist(role);
		entityManager.flush();

		List<Role> roles = roleRepository.findByNames(List.of("test_role"));
		assertEquals(1, roles.size());

		entityManager.remove(role);
	}

	@Test
	public void shouldFindTwoRole() {
		Role role1 = new Role();
		role1.setName("test_role_one");
		Role role2 = new Role();
		role2.setName("test_role_two");
		entityManager.persist(role1);
		entityManager.persist(role2);
		entityManager.flush();

		List<Role> roles = roleRepository.findByNames(List.of("test_role_one", "test_role_two"));
		assertEquals(2, roles.size());
	}

	@Test
	public void shouldFindRoleByName() {
		Role role1 = new Role();
		role1.setName("test_role_one");
		entityManager.persist(role1);
		entityManager.flush();

		Optional<Role> optRole = roleRepository.findByName("test_role_one");
		assertTrue(optRole.isPresent());
	}
}
