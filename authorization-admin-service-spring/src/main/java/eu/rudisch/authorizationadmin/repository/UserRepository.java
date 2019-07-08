package eu.rudisch.authorizationadmin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.rudisch.authorizationadmin.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String name);
}
