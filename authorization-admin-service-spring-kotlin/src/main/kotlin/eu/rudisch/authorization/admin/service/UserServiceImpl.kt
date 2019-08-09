package eu.rudisch.authorization.admin.service

import eu.rudisch.authorization.admin.model.Role
import eu.rudisch.authorization.admin.model.User
import eu.rudisch.authorization.admin.web.controller.resource.UserRestRep
import eu.rudisch.authorization.admin.repository.UserRepository
import eu.rudisch.authorization.admin.repository.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.Optional
import java.util.stream.Collectors
import kotlin.collections.ArrayList

@Service
class UserServiceImpl : UserService {

	@Autowired
	private val userRepository: UserRepository? = null

	@Autowired
	private val roleRepository: RoleRepository? = null

	override fun getUserRepresentations(): List<UserRestRep> {
		return userRepository!!.findAll().stream()
				.map { UserRestRep.fromUser(it) }
				.collect(Collectors.toList<UserRestRep>())
	}

	override fun updateUser(id: Int, toUpdate: UserRestRep, issuer: String): UserRestRep {
		val user = userRepository!!.getOne(id)
		val password = getPassword(toUpdate, user)
		val roles = checkRoles(user.roles, roleRepository!!.findByNames(toUpdate.roleNames))

		val userToStore = if (user.username == issuer)
			updatePartial(toUpdate, user, password, roles)
		else
			updateComplete(toUpdate, password, roles)

		userRepository.save(userToStore)
		return UserRestRep.fromUser(userToStore)
	}

	internal fun getPassword(toUpdate: UserRestRep, user: User): String {
		return if (toUpdate.password == toUpdate.passwordRepeat)
			toUpdate.password
		else
			user.password
	}

	internal fun updateComplete(toUpdate: UserRestRep, password: String, roles: List<Role>): User {
		return User(toUpdate.id,
				toUpdate.username,
				password,
				toUpdate.email,
				toUpdate.enabled,
				!toUpdate.accountExpired,
				!toUpdate.accountLocked,
				!toUpdate.credentialsExpired,
				roles)
	}

	internal fun updatePartial(toUpdate: UserRestRep, user: User, password: String, roles: List<Role>): User {
		return User(toUpdate.id,
				user.username,
				password,
				toUpdate.email,
				user.enabled,
				user.accountNonExpired,
				user.accountNonLocked,
				user.credentialsNonExpired,
				roles)
	}

	internal fun checkRoles(existing: List<Role>, toReplace: List<Role>): List<Role> {
		val ret = ArrayList(toReplace)
		val existingAuthAdmin = checkForRole(existing, RoleRepository.ROLE_AUTH_ADMIN)
		val toReplaceAuthAdmin = checkForRole(toReplace, RoleRepository.ROLE_AUTH_ADMIN)
		if (existingAuthAdmin.isPresent && !toReplaceAuthAdmin.isPresent)
			ret.add(existingAuthAdmin.get())
		return ret
	}

	internal fun checkForRole(roles: List<Role>, role: String): Optional<Role> {
		return roles.stream()
				.filter { r -> r.name == role }
				.findFirst()
	}
}