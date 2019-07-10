package eu.rudisch.authorizationadmin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.rudisch.authorizationadmin.model.Role;
import eu.rudisch.authorizationadmin.model.User;
import eu.rudisch.authorizationadmin.repository.RoleRepository;
import eu.rudisch.authorizationadmin.repository.UserRepository;
import eu.rudisch.authorizationadmin.web.controller.resource.UserRestRep;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public List<UserRestRep> getUserRepresentations() {
		List<UserRestRep> usersRestReps = userRepository.findAll().stream()
				.map(UserRestRep::fromUser)
				.collect(Collectors.toList());
		return usersRestReps;
	}

	@Override
	public UserRestRep updateUser(int id, UserRestRep toUpdate, String issuer) {
		User user = userRepository.getOne(id);
		boolean selfUpdate = user != null && user.getUsername().equals(issuer);

		String password = toUpdate.getPassword() != null && toUpdate.getPassword().equals(toUpdate.getPasswordRepeat())
				? toUpdate.getPassword()
				: user.getPassword();

		if (!selfUpdate) {
			user.setUsername(toUpdate.getUsername());
			user.setEnabled(toUpdate.isEnabled());
			user.setAccountNonExpired(!toUpdate.isAccountExpired());
			user.setAccountNonLocked(!toUpdate.isAccountLocked());
		}
		user.setPassword(password);
		user.setEmail(toUpdate.getEmail());
		user.setCredentialsNonExpired(!toUpdate.isCredentialsExpired());

		List<Role> roles = checkRoles(user.getRoles(), roleRepository.findByNames(toUpdate.getRoleNames()));
		user.setRoles(roles);

		userRepository.save(user);

		return UserRestRep.fromUser(user);
	}

	List<Role> checkRoles(List<Role> existing, List<Role> toReplace) {
		List<Role> ret = new ArrayList<>(toReplace);
		Optional<Role> existingAuthAdmin = checkForRole(existing, RoleRepository.ROLE_AUTH_ADMIN);
		Optional<Role> toReplaceAuthAdmin = checkForRole(toReplace, RoleRepository.ROLE_AUTH_ADMIN);
		if (existingAuthAdmin.isPresent() && !toReplaceAuthAdmin.isPresent())
			ret.add(existingAuthAdmin.get());
		return ret;
	}

	Optional<Role> checkForRole(List<Role> roles, String role) {
		return roles.stream()
				.filter(r -> r.getName().equals(role))
				.findFirst();
	}

}
