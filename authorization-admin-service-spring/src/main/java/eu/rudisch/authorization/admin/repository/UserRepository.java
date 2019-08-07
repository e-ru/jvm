package eu.rudisch.authorization.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import eu.rudisch.authorization.admin.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	Optional<User> findByUsername(String name);
}
