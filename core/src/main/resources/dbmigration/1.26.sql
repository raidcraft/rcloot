-- apply changes
create table rc_loot_objects (
  id                            bigint auto_increment not null,
  loot_table                    varchar(255) default '' not null,
  world                         varchar(255) not null,
  x                             integer not null,
  y                             integer not null,
  z                             integer not null,
  enabled                       tinyint(1) default 0 not null,
  cooldown                      integer not null,
  infinite                      tinyint(1) default 0 not null,
  public_loot_object            tinyint(1) default 0 not null,
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_rc_loot_objects primary key (id)
);

create table rc_loot_players (
  id                            integer auto_increment not null,
  player_id                     varchar(40) not null,
  loot_object_id                bigint not null,
  created                       datetime(6) not null,
  constraint pk_rc_loot_players primary key (id)
);

create index ix_rc_loot_players_loot_object_id on rc_loot_players (loot_object_id);
alter table rc_loot_players add constraint fk_rc_loot_players_loot_object_id foreign key (loot_object_id) references rc_loot_objects (id) on delete restrict on update restrict;

