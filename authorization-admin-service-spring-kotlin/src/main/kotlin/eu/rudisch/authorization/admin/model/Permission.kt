package eu.rudisch.authorization.admin.model

import java.io.Serializable

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Entity
@Table(name = "permission")
data class Permission(
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		val id: Int,
		@Column(name = "name")
		val name: String
) : Serializable