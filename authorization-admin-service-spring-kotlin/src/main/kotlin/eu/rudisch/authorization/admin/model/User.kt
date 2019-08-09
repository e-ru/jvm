package eu.rudisch.authorization.admin.model

import java.io.Serializable
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.Table

@Entity
@Table(name = "user")
data class User	(
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		val id: Int,

		@Column(name = "username")
		val username: String,
		@Column(name = "password")
		val password: String,
		@Column(name = "email")
		val email: String,
		@Column(name = "enabled")
		val enabled: Boolean,
		@Column(name = "accountNonExpired")
		val accountNonExpired: Boolean,
		@Column(name = "credentialsNonExpired")
		val credentialsNonExpired: Boolean,
		@Column(name = "accountNonLocked")
		val accountNonLocked: Boolean,

		@JvmSuppressWildcards
		@ManyToMany(cascade= [CascadeType.ALL], fetch = FetchType.EAGER)
		@JoinTable(name = "role_user",
				joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
				inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")])
		val roles: List<Role>
) : Serializable
