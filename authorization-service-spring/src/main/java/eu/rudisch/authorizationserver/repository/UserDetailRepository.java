package eu.rudisch.authorizationserver.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.rudisch.authorizationserver.model.User;

public interface UserDetailRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String name);
}
