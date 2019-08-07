package eu.rudisch.authorization.admin.model

import java.io.Serializable
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
		var username: String,
		@Column(name = "password")
		var password: String,
		@Column(name = "email")
		var email: String,
		@Column(name = "enabled")
		var enabled: Boolean,
		@Column(name = "accountNonExpired")
		var accountNonExpired: Boolean,
		@Column(name = "credentialsNonExpired")
		var credentialsNonExpired: Boolean,
		@Column(name = "accountNonLocked")
		var accountNonLocked: Boolean,

		@JvmSuppressWildcards
		@ManyToMany(fetch = FetchType.EAGER)
		@JoinTable(name = "role_user",
				joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
				inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")])
		var roles: List<Role>
) : Serializable