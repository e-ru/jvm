package eu.rudisch.users.rest.model;

public final class Account {

	private String accountName = null;
	private String accountEmailAddress = null;
	private String accountPhoneNumber = null;

	private Account() {
	}

	public String getAccountName() {
		return accountName;
	}

	public String getAccountEmailAddress() {
		return accountEmailAddress;
	}

	public String getAccountPhoneNumber() {
		return accountPhoneNumber;
	}

	public static Account fromParameter(String accountName, String accountEmailAddress, String accountPhoneNumber) {
		Account account = new Account();

		account.accountName = accountName;
		account.accountEmailAddress = accountEmailAddress;
		account.accountPhoneNumber = accountPhoneNumber;

		return account;
	}

}
