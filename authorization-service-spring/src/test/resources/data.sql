INSERT INTO oauth_client_details (
	client_id, 
	client_secret, 
	web_server_redirect_uri,
	access_token_validity, 
	refresh_token_validity, 
	resource_ids, 
	authorized_grant_types,
	authorities,
	additional_information
) VALUES (
	'auth_server', 
	'{noop}', 
	'http://localhost:8889/redirect.html',
	'3600', 
	'10000', 
	'oauth2-control-resource', 
	'authorization_code,password,refresh_token',
	'read_oauth,create_oauth,update_oauth,delete_oauth',
	'{}'
);

INSERT INTO PERMISSION (
	NAME
) VALUES
 	('read_oauth'),
 	('create_oauth'),
 	('update_oauth'),
 	('delete_oauth');

INSERT INTO role (
 	NAME
) VALUES
	('ROLE_oauth_admin'),
	('ROLE_oauth_viewer');

INSERT INTO PERMISSION_ROLE (
	PERMISSION_ID, 
	ROLE_ID
) VALUES
	(1,1),
	(2,1),
	(3,1),
	(4,1), 
	(1,2);
     
INSERT INTO USER (
	id, 
	username,
	password, 
	email, 
	enabled, 
	accountNonExpired, 
	credentialsNonExpired, 
	accountNonLocked
) VALUES (
	'1', 
	'eru',
	'{bcrypt}$2a$10$cwcOgGpz.UdW0Pd/rqnFlOf1bzII.G933uxfu2iZBa9rtiTNUOKgm',
	'e@ru.org', 
	'1', 
	'1', 
	'1', 
	'1'
);

INSERT INTO USER (
	id, 
	username,
	password, 
	email, 
	enabled, 
	accountNonExpired, 
	credentialsNonExpired, 
	accountNonLocked
) VALUES (
	'2', 
	'rru', 
	'{bcrypt}$2a$10$e/7JPkJSy30ty.wV/YAvs.NP.YyK15VWU9dGc1LYIlVORkrcxf5tm',
	'r@ru.org', 
	'1', 
	'1', 
	'1', 
	'1'
);

INSERT INTO USER (
	id, 
	username,
	password, 
	email, 
	enabled, 
	accountNonExpired, 
	credentialsNonExpired, 
	accountNonLocked
) VALUES (
	'3', 
	'rru2', 
	'{bcrypt}$2a$10$zW0X9Zf3O7VBjoF0p9VrmeCEOGvweqRTTHlJkRhXBW1e5Na18/rga',
	'r@ru.org', 
	'1', 
	'1', 
	'1', 
	'1'
);

INSERT INTO ROLE_USER (
	ROLE_ID, 
	USER_ID
) VALUES (
	1, 
	1
), (
	2, 
	2
), (
	2, 
	3
);
