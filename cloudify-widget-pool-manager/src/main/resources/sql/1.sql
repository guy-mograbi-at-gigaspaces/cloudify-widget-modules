create table nodes (
  id int not null auto_increment,
  pool_id varchar(200),
  node_status varchar(50),
  machine_id varchar(200),
  cloudify_version varchar(20),
  primary key (id)
);