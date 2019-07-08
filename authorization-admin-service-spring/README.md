// hasRole: role of the user profile in database
	// hasAuthority: permission of user profile in database
	// #oauth2.hasScope: granted scope during access token creation

	private static final String RESOURCE_ID = "oauth2-control-resource";
//	private static final String ADMIN_CREATE_SCOPE = "hasRole('ROLE_oauth_admin') and hasAuthority('create_oauth')";
	private static final String ADMIN_CREATE_SCOPE = "hasRole('ROLE_oauth_admin') or #oauth2.hasScope('create_oauth')";
	private static final String ADMIN_READ_SCOPE = "hasAuthority('read_oauth')";
	private static final String ADMIN_UPDATE_SCOPE = "hasRole('ROLE_oauth_admin') and hasAuthority('update_oauth')";
	private static final String ADMIN_DELETE_SCOPE = "hasRole('ROLE_oauth_admin') and hasAuthority('delete_oauth')";
//	private static final String TOKEN_DELETE_SCOPE = "hasAuthority('revoke_refresh_token')";
//	private static final String SECURED_WRITE_SCOPE = "#oauth2.hasScope('WRITE')";
	private static final String ADMIN_PATTERN = "/admin/**";
	private static final String TOKENS_PATTERN = "/tokens/refreshTokens";
