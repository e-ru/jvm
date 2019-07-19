DROP TABLE if exists oauth_client_details;
DROP TABLE if exists permission_role;
DROP TABLE if exists role_user;
DROP TABLE if exists permission;
DROP TABLE if exists role;
DROP TABLE if exists user;
DROP TABLE if exists oauth_refresh_token;

create table if not exists  oauth_client_details (
  client_id varchar(255) not null,
  client_secret varchar(255) not null,
  web_server_redirect_uri varchar(2048) default null,
  scope varchar(255) default null,
  access_token_validity int(11) default null,
  refresh_token_validity int(11) default null,
  resource_ids varchar(1024) default null,
  authorized_grant_types varchar(1024) default null,
  authorities varchar(1024) default null,
  additional_information varchar(4096) default null,
  autoapprove varchar(255) default null,
  primary key (client_id)
);

create table if not exists  permission (
  id int(11) not null auto_increment,
  name varchar(512) default null,
  primary key (id)
);

create table if not exists role (
  id int(11) not null auto_increment,
  name varchar(255) default null,
  primary key (id)
);

create table if not exists  user (
  id int(11) not null auto_increment,
  username varchar(100) not null,
  password varchar(1024) not null,
  email varchar(1024) not null,
  enabled tinyint(4) not null,
  accountNonExpired tinyint(4) not null,
  credentialsNonExpired tinyint(4) not null,
  accountNonLocked tinyint(4) not null,
  primary key (id)
);

create table  if not exists permission_role (
  permission_id int(11) default null,
  role_id int(11) default null,
  constraint permission_role_ibfk_1 foreign key (permission_id) references permission (id),
  constraint permission_role_ibfk_2 foreign key (role_id) references role (id)
);

create table if not exists role_user (
  role_id int(11) default null,
  user_id int(11) default null,
  constraint role_user_ibfk_1 foreign key (role_id) references role (id),
  constraint role_user_ibfk_2 foreign key (user_id) references user (id)
);

create table if not exists oauth_refresh_token (
  token_id VARCHAR(256),
  client_id varchar(255),
  username varchar(100),
  token BLOB,
  authentication BLOB
);

