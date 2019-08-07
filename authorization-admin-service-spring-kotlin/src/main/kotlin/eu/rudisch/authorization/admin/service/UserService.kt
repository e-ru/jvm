package eu.rudisch.authorization.admin.service

import eu.rudisch.authorization.admin.web.controller.resource.UserRestRep

interface UserService {
	fun getUserRepresentations(): List<UserRestRep>
	fun updateUser(id: Int, toUpdate: UserRestRep, issuer: String): UserRestRep
}