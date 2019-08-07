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
		val selfUpdate = user.username == issuer

		val password = if (toUpdate.password != null && toUpdate.password == toUpdate.passwordRepeat)
			toUpdate.password
		else
			user.password

		if (!selfUpdate) {
			user.username = toUpdate.username
			user.enabled = toUpdate.enabled
			user.accountNonExpired = !toUpdate.accountExpired
			user.accountNonLocked = !toUpdate.accountLocked
		}
		user.password = password
		user.email = toUpdate.email
		user.credentialsNonExpired = !toUpdate.credentialsExpired

		val roles = checkRoles(user.roles, roleRepository!!.findByNames(toUpdate.roleNames))
		user.roles = roles

		userRepository.save(user)

		return UserRestRep.fromUser(user)
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