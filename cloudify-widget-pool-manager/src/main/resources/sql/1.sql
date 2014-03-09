create table nodes (
  id int not null auto_increment,
  pool_id varchar(200),
  node_status varchar(200),
  machine_id varchar(200),
  cloudify_version varchar(20),
  primary key (id)
);

create table task_errors (
  id int not null auto_increment,
  task_name varchar(200),
  pool_id varchar(200),
  message varchar(2000),
  info varchar(4000),
  primary key (id)
);
