package eu.rudisch.users;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eu.rudisch.users.persistance.SqlService;
import eu.rudisch.users.persistance.model.Account;
import eu.rudisch.users.persistance.model.Login;
import eu.rudisch.users.persistance.model.Membership;
import eu.rudisch.users.persistance.model.Role;
import eu.rudisch.users.persistance.model.UserDetail;

public class Main {
	public static void main(String[] args) {
		SqlService sqlService = new SqlService("users-service-jpa");

		select(sqlService);
	}

	private static void select(SqlService sqlService) {
		var l = sqlService.getUserDetails();
		l.forEach(ud -> {
			ud.getLogins().forEach(lo -> {
				System.out.println(lo.getUserName());
			});
			ud.getMemberships().forEach(m -> {
				System.out.println(m.getAccount().getEmailAddress());
				System.out.println(m.getRole().getRole());
			});
			System.out.println(ud.getFirstName());
		});
	}

	private static void insert(SqlService sqlService) {
		Role role = new Role();
		role.setRole("member");

		Account account = new Account();
		account.setEmailAddress("bob@smith.com");
		account.setPhoneNumber("0123455");

		Membership membership = new Membership();
		membership.setAccount(account);
		membership.setRole(role);

		Login login = new Login();
		login.setUserName("bos2");
		login.setPasswordHash(getHash("test"));
		login.setPasswordSalt("1243");

		UserDetail userDetail = new UserDetail();
		userDetail.setFirstName("Bob");
		userDetail.setLastName("Smith");

		userDetail.addLogin(login);
		userDetail.addMembership(membership);

		sqlService.insertUserDetail(userDetail);
	}

	private static String getHash(String s) {
		try {
			// getInstance() method is called with algorithm SHA-1
			MessageDigest md = MessageDigest.getInstance("SHA-1");

			// digest() method is called
			// to calculate message digest of the input string
			// returned as array of byte
			byte[] messageDigest = md.digest(s.getBytes());

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, messageDigest);

			// Convert message digest into hex value
			String hashtext = no.toString(16);

			// Add preceding 0s to make it 32 bit
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			// return the HashText
			return hashtext;
		} // For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}
}
