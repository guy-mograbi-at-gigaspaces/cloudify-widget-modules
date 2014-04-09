-- tasks is no longer needed as it's saved to memory
drop table tasks;
-- supporting ssh persistence now as they're not kept after machine is created
alter table nodes add column machine_ssh_details varchar(32768);
