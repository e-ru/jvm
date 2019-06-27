package eu.rudisch.authorizationserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.rudisch.authorizationserver.model.Permission;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

}
