package eu.rudisch.users.rest.model;

public final class Account {

	private String accountName = null;

	private Account() {
	}

	public String getAccountName() {
		return accountName;
	}

	public static Account fromParameter(String accountName) {
		Account account = new Account();
		account.accountName = accountName;
		return account;
	}

}
