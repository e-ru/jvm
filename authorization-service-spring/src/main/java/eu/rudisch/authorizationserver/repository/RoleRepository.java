package eu.rudisch.authorizationserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.rudisch.authorizationserver.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
