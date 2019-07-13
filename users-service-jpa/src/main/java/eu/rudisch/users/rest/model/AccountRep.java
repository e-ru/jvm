package eu.rudisch.users.rest.model;

public final class AccountRep {

	private String accountName = null;

	private AccountRep() {
	}

	public String getAccountName() {
		return accountName;
	}

	public static AccountRep fromParameter(String accountName) {
		AccountRep account = new AccountRep();
		account.accountName = accountName;
		return account;
	}

}
