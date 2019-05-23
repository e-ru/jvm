ALTER TABLE login DROP FOREIGN KEY FK_login_user_id
ALTER TABLE membership DROP FOREIGN KEY FK_membership_user_id
ALTER TABLE membership_account DROP FOREIGN KEY FK_membership_account_membership_id
ALTER TABLE membership_account DROP FOREIGN KEY FK_membership_account_account_id
ALTER TABLE membership_role DROP FOREIGN KEY FK_membership_role_membership_id
ALTER TABLE membership_role DROP FOREIGN KEY FK_membership_role_role_id
DROP TABLE account
DROP TABLE login
DROP TABLE membership
DROP TABLE role
DROP TABLE user_details
DROP TABLE membership_account
DROP TABLE membership_role
