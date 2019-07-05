package eu.rudisch.authorizationserver.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import eu.rudisch.authorizationserver.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	public static final String ROLE_AUTH_ADMIN = "ROLE_oauth_admin";

	Optional<Role> findByName(String name);

	@Query("select r from Role r where r.name in :names")
	List<Role> findByNames(@Param("names") List<String> names);
}
