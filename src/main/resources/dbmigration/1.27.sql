-- apply changes
alter table rc_loot_objects add column destroyable tinyint(1) default 0 not null;
alter table rc_loot_objects add column destroyed datetime(6);
alter table rc_loot_objects add column material varchar(255);

