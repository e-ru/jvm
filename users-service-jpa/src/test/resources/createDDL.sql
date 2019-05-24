CREATE TABLE account (ID INTEGER AUTO_INCREMENT NOT NULL, name VARCHAR(255), PRIMARY KEY (ID));
CREATE TABLE login (ID INTEGER AUTO_INCREMENT NOT NULL, password_hash VARCHAR(255), password_salt VARCHAR(255), user_name VARCHAR(255) UNIQUE, user_id INTEGER, PRIMARY KEY (ID));
CREATE TABLE membership (ID INTEGER AUTO_INCREMENT NOT NULL, account_emailaddress VARCHAR(255), account_phonenumber VARCHAR(255), user_id INTEGER, PRIMARY KEY (ID));
CREATE TABLE role (ID INTEGER AUTO_INCREMENT NOT NULL, role VARCHAR(255), PRIMARY KEY (ID));
CREATE TABLE user_details (ID INTEGER AUTO_INCREMENT NOT NULL, first_name VARCHAR(255), last_name VARCHAR(255), PRIMARY KEY (ID));
CREATE TABLE membership_account (account_id INTEGER NOT NULL, membership_id INTEGER NOT NULL, PRIMARY KEY (account_id, membership_id));
CREATE TABLE membership_role (membership_id INTEGER NOT NULL, role_id INTEGER NOT NULL, PRIMARY KEY (membership_id, role_id));
ALTER TABLE login ADD CONSTRAINT FK_login_user_id FOREIGN KEY (user_id) REFERENCES user_details (ID);
ALTER TABLE membership ADD CONSTRAINT FK_membership_user_id FOREIGN KEY (user_id) REFERENCES user_details (ID);
ALTER TABLE membership_account ADD CONSTRAINT FK_membership_account_membership_id FOREIGN KEY (membership_id) REFERENCES membership (ID);
ALTER TABLE membership_account ADD CONSTRAINT FK_membership_account_account_id FOREIGN KEY (account_id) REFERENCES account (ID);
ALTER TABLE membership_role ADD CONSTRAINT FK_membership_role_membership_id FOREIGN KEY (membership_id) REFERENCES membership (ID);
ALTER TABLE membership_role ADD CONSTRAINT FK_membership_role_role_id FOREIGN KEY (role_id) REFERENCES role (ID);
