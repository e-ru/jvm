package eu.rudisch.authorization.admin.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import eu.rudisch.authorization.admin.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	String ROLE_AUTH_ADMIN = "ROLE_oauth_admin";

	Optional<Role> findByName(String name);

	@Query("select r from Role r where r.name in :names")
	List<Role> findByNames(@Param("names") List<String> names);
}
