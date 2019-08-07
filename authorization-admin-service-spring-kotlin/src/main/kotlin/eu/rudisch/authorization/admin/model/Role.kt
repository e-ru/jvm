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
@Table(name = "role")
data class Role(
		@Id
		@GeneratedValue(strategy = GenerationType.AUTO)
		val id: Int,

		@Column(name = "name")
		val name: String,

		@ManyToMany(fetch = FetchType.EAGER)
		@JoinTable(name = "permission_role",
				joinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")],
				inverseJoinColumns = [JoinColumn(name = "permission_id", referencedColumnName = "id")])
		val permissions: MutableList<Permission>
) : Serializable {
}