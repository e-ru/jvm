package eu.rudisch.authorizationserver.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.rudisch.authorizationserver.model.Role;
import eu.rudisch.authorizationserver.model.User;
import eu.rudisch.authorizationserver.model.UserRestRep;
import eu.rudisch.authorizationserver.repository.RoleRepository;
import eu.rudisch.authorizationserver.repository.UserRepository;

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
	public UserRestRep updateUser(int id, UserRestRep toUpdate) {
		User user = userRepository.getOne(id);

		List<Role> roles = roleRepository.findByNames(toUpdate.getRoleNames());
		String password = toUpdate.getPassword() != null && toUpdate.getPassword().equals(toUpdate.getPasswordRepeat())
				? toUpdate.getPassword()
				: user.getPassword();

		user.setUsername(toUpdate.getUsername());
		user.setPassword(password);
		user.setEmail(toUpdate.getEmail());
		user.setEnabled(toUpdate.isEnabled());
		user.setAccountNonExpired(!toUpdate.isAccountExpired());
		user.setAccountNonLocked(!toUpdate.isAccountLocked());
		user.setCredentialsNonExpired(!toUpdate.isCredentialsExpired());
		user.setRoles(roles);

		userRepository.save(user);

		return UserRestRep.fromUser(user);
	}

}
