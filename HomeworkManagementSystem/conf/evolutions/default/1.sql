# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table token (
  id                        bigint auto_increment not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   datetime,
  expires                   datetime,
  constraint ck_token_type check (type in ('EV','PR')),
  constraint uq_token_token unique (token),
  constraint pk_token primary key (id))
;

create table assessments (
  id                        bigint auto_increment not null,
  duty_id                   bigint,
  proofreader_id            bigint,
  mod_time                  bigint not null,
  constraint pk_assessments primary key (id))
;

create table assignments (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  description               Text not null,
  deadline                  datetime,
  multiplicator             integer,
  comment                   varchar(255),
  additional                tinyint(1) default 0,
  completed                 tinyint(1) default 0,
  lecture_id                bigint,
  mod_time                  bigint not null,
  constraint pk_assignments primary key (id))
;

create table bonuses (
  id                        bigint auto_increment not null,
  percentage                integer not null,
  bonus                     float not null,
  lecture_id                bigint,
  mod_time                  bigint not null,
  constraint pk_bonuses primary key (id))
;

create table dutys (
  id                        bigint auto_increment not null,
  uploaded_file             varchar(255) not null,
  uploaded_date_time        datetime not null,
  catched                   tinyint(1) default 0,
  corrected                 tinyint(1) default 0,
  assignment_id             bigint,
  assessment_id             bigint,
  user_id                   bigint,
  mod_time                  bigint not null,
  constraint pk_dutys primary key (id))
;

create table lectures (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  description               Text not null,
  closingdate               datetime,
  optional_dutys            integer,
  lower_procentual_boundery_of_dutys integer,
  minimum_percentage_for_examination integer,
  semester_id               bigint,
  mod_time                  bigint not null,
  constraint pk_lectures primary key (id))
;

create table messages (
  id                        bigint auto_increment not null,
  body                      Text,
  date                      datetime,
  parent_id                 bigint,
  duty_id                   bigint,
  lecture_id                bigint,
  assignment_id             bigint,
  sender_id                 bigint,
  mod_time                  bigint not null,
  constraint pk_messages primary key (id))
;

create table roles (
  name                      varchar(255) not null,
  mod_time                  bigint not null,
  constraint pk_roles primary key (name))
;

create table semesters (
  id                        bigint auto_increment not null,
  semester                  varchar(255) not null,
  mod_time                  bigint not null,
  constraint pk_semesters primary key (id))
;

create table stuff (
  id                        bigint auto_increment not null,
  file_name                 varchar(255) not null,
  type                      varchar(255),
  assignment_id             bigint,
  mod_time                  bigint not null,
  constraint pk_stuff primary key (id))
;

create table subtasks (
  id                        bigint auto_increment not null,
  name                      varchar(255) not null,
  description               Text not null,
  points                    float not null,
  additional                tinyint(1) default 0,
  assignment_id             bigint,
  mod_time                  bigint not null,
  constraint pk_subtasks primary key (id))
;

create table users (
  id                        bigint auto_increment not null,
  email                     varchar(255),
  first_name                varchar(255) not null,
  last_name                 varchar(255) not null,
  password                  varchar(255) not null,
  matrikel_number           varchar(255),
  last_login                datetime,
  email_validated           tinyint(1) default 0,
  role_name                 varchar(255),
  mod_time                  bigint not null,
  constraint uq_users_email unique (email),
  constraint uq_users_matrikel_number unique (matrikel_number),
  constraint pk_users primary key (id))
;

create table valuations (
  id                        bigint auto_increment not null,
  resultpoints              float,
  comment                   varchar(255),
  corrected                 tinyint(1) default 0,
  assessment_id             bigint,
  subtask_id                bigint,
  mod_time                  bigint not null,
  constraint pk_valuations primary key (id))
;


create table users_lectures (
  lectures_id                    bigint not null,
  users_id                       bigint not null,
  constraint pk_users_lectures primary key (lectures_id, users_id))
;

create table users_lectureadmins (
  lectures_id                    bigint not null,
  users_id                       bigint not null,
  constraint pk_users_lectureadmins primary key (lectures_id, users_id))
;

create table users_proofreaders (
  lectures_id                    bigint not null,
  users_id                       bigint not null,
  constraint pk_users_proofreaders primary key (lectures_id, users_id))
;
alter table assessments add constraint fk_assessments_duty_1 foreign key (duty_id) references dutys (id) on delete restrict on update restrict;
create index ix_assessments_duty_1 on assessments (duty_id);
alter table assessments add constraint fk_assessments_proofreader_2 foreign key (proofreader_id) references users (id) on delete restrict on update restrict;
create index ix_assessments_proofreader_2 on assessments (proofreader_id);
alter table assignments add constraint fk_assignments_lecture_3 foreign key (lecture_id) references lectures (id) on delete restrict on update restrict;
create index ix_assignments_lecture_3 on assignments (lecture_id);
alter table bonuses add constraint fk_bonuses_lecture_4 foreign key (lecture_id) references lectures (id) on delete restrict on update restrict;
create index ix_bonuses_lecture_4 on bonuses (lecture_id);
alter table dutys add constraint fk_dutys_assignment_5 foreign key (assignment_id) references assignments (id) on delete restrict on update restrict;
create index ix_dutys_assignment_5 on dutys (assignment_id);
alter table dutys add constraint fk_dutys_assessment_6 foreign key (assessment_id) references assessments (id) on delete restrict on update restrict;
create index ix_dutys_assessment_6 on dutys (assessment_id);
alter table dutys add constraint fk_dutys_user_7 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_dutys_user_7 on dutys (user_id);
alter table lectures add constraint fk_lectures_semester_8 foreign key (semester_id) references semesters (id) on delete restrict on update restrict;
create index ix_lectures_semester_8 on lectures (semester_id);
alter table messages add constraint fk_messages_parent_9 foreign key (parent_id) references messages (id) on delete restrict on update restrict;
create index ix_messages_parent_9 on messages (parent_id);
alter table messages add constraint fk_messages_duty_10 foreign key (duty_id) references dutys (id) on delete restrict on update restrict;
create index ix_messages_duty_10 on messages (duty_id);
alter table messages add constraint fk_messages_lecture_11 foreign key (lecture_id) references lectures (id) on delete restrict on update restrict;
create index ix_messages_lecture_11 on messages (lecture_id);
alter table messages add constraint fk_messages_assignment_12 foreign key (assignment_id) references assignments (id) on delete restrict on update restrict;
create index ix_messages_assignment_12 on messages (assignment_id);
alter table messages add constraint fk_messages_sender_13 foreign key (sender_id) references users (id) on delete restrict on update restrict;
create index ix_messages_sender_13 on messages (sender_id);
alter table stuff add constraint fk_stuff_assignment_14 foreign key (assignment_id) references assignments (id) on delete restrict on update restrict;
create index ix_stuff_assignment_14 on stuff (assignment_id);
alter table subtasks add constraint fk_subtasks_assignment_15 foreign key (assignment_id) references assignments (id) on delete restrict on update restrict;
create index ix_subtasks_assignment_15 on subtasks (assignment_id);
alter table users add constraint fk_users_role_16 foreign key (role_name) references roles (name) on delete restrict on update restrict;
create index ix_users_role_16 on users (role_name);
alter table valuations add constraint fk_valuations_assessment_17 foreign key (assessment_id) references assessments (id) on delete restrict on update restrict;
create index ix_valuations_assessment_17 on valuations (assessment_id);
alter table valuations add constraint fk_valuations_subtask_18 foreign key (subtask_id) references subtasks (id) on delete restrict on update restrict;
create index ix_valuations_subtask_18 on valuations (subtask_id);



alter table users_lectures add constraint fk_users_lectures_lectures_01 foreign key (lectures_id) references lectures (id) on delete restrict on update restrict;

alter table users_lectures add constraint fk_users_lectures_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_lectureadmins add constraint fk_users_lectureadmins_lectures_01 foreign key (lectures_id) references lectures (id) on delete restrict on update restrict;

alter table users_lectureadmins add constraint fk_users_lectureadmins_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_proofreaders add constraint fk_users_proofreaders_lectures_01 foreign key (lectures_id) references lectures (id) on delete restrict on update restrict;

alter table users_proofreaders add constraint fk_users_proofreaders_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table token;

drop table assessments;

drop table assignments;

drop table bonuses;

drop table dutys;

drop table lectures;

drop table users_lectures;

drop table users_lectureadmins;

drop table users_proofreaders;

drop table messages;

drop table roles;

drop table semesters;

drop table stuff;

drop table subtasks;

drop table users;

drop table valuations;

SET FOREIGN_KEY_CHECKS=1;

