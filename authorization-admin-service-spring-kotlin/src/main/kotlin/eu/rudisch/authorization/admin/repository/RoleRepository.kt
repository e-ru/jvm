package eu.rudisch.authorization.admin.repository

import eu.rudisch.authorization.admin.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface RoleRepository : JpaRepository<Role, Int> {
	companion object {
		const val ROLE_AUTH_ADMIN = "ROLE_oauth_admin"
	}

	fun findByName(name: String): Optional<Role>

	@Query("select r from Role r where r.name in :names")
	fun findByNames(@Param("names") names: List<String>): List<Role>
}