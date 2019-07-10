package eu.rudisch.authorizationadmin.web.controller.resource;

import java.util.List;
import java.util.stream.Collectors;

import eu.rudisch.authorizationadmin.model.Role;
import eu.rudisch.authorizationadmin.model.User;
import lombok.Data;

@Data
public class UserRestRep {

	private int id;
	private String username;
	private String password;
	private String passwordRepeat;
	private String email;
	private boolean enabled;
	private boolean accountExpired;
	private boolean credentialsExpired;
	private boolean accountLocked;
	private List<String> roleNames;

	public static UserRestRep fromUser(User user) {
		UserRestRep userRestRep = new UserRestRep();

		userRestRep.id = user.getId();
		userRestRep.username = user.getUsername();
		userRestRep.password = user.getPassword();
		userRestRep.email = user.getEmail();
		userRestRep.enabled = user.isEnabled();
		userRestRep.accountExpired = !user.isAccountNonExpired();
		userRestRep.accountLocked = !user.isAccountNonLocked();
		userRestRep.credentialsExpired = !user.isCredentialsNonExpired();
		userRestRep.roleNames = user.getRoles().stream()
				.map(Role::getName)
				.collect(Collectors.toList());

		return userRestRep;
	}
}
