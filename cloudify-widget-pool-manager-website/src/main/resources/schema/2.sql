create table resource (
  id BIGINT NOT NULL AUTO_INCREMENT primary key,
  account_id BIGINT NOT NULL,
  resource_content blob,
  resource_name varchar (255),
  resource_content_type varchar(255),
  resource_size BIGINT,
  resource_orig_name varchar (511)
);


-- drop table resource