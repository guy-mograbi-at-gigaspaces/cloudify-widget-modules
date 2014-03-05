CREATE TABLE pool_configuration ( id BIGINT NOT NULL AUTO_INCREMENT, account_id BIGINT, pool_setting TEXT, PRIMARY KEY (id) );
CREATE TABLE `patchlevel` ( `version` bigint(20) NOT NULL DEFAULT '0',  PRIMARY KEY (`version`));
CREATE TABLE account ( id BIGINT NOT NULL AUTO_INCREMENT, uuid VARCHAR(255) DEFAULT NULL, PRIMARY KEY (id) );


-- drop table pool_configuration;
-- drop table patchlevel;
-- drop table account;