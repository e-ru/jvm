package eu.rudisch.authorization.admin.repository

import eu.rudisch.authorization.admin.model.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Int> {
	fun findByUsername(name: String): Optional<User>
}