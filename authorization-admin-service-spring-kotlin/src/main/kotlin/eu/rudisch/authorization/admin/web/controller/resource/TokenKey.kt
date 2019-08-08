package eu.rudisch.authorization.admin.web.controller.resource

data class TokenKey(val alg: String, val value: String) {
	constructor() : this("","") // needed for jackson deserialization
}