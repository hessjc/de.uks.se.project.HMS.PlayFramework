# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table assessments (
  id                        bigint not null,
  duty_id                   bigint,
  proofreader_id            bigint,
  constraint pk_assessments primary key (id))
;

create table assignments (
  id                        bigint not null,
  name                      varchar(255),
  description               Text,
  deadline                  timestamp,
  multiplicator             integer,
  comment                   varchar(255),
  additional                boolean,
  completed                 boolean,
  lecture_id                bigint,
  constraint pk_assignments primary key (id))
;

create table dutys (
  id                        bigint not null,
  uploaded_file             varchar(255),
  uploaded_date_time        timestamp,
  catched                   boolean,
  corrected                 boolean,
  assignment_id             bigint,
  assessment_id             bigint,
  user_id                   bigint,
  constraint pk_dutys primary key (id))
;

create table lectures (
  id                        bigint not null,
  name                      varchar(255),
  description               Text,
  closingdate               timestamp,
  semester_id               bigint,
  constraint pk_lectures primary key (id))
;

create table roles (
  name                      varchar(255) not null,
  constraint pk_roles primary key (name))
;

create table semesters (
  id                        bigint not null,
  semester                  varchar(255),
  constraint pk_semesters primary key (id))
;

create table stuff (
  id                        bigint not null,
  file_path                 varchar(255),
  file_name                 varchar(255),
  type                      varchar(255),
  assignment_id             bigint,
  constraint pk_stuff primary key (id))
;

create table subtasks (
  id                        bigint not null,
  name                      varchar(255),
  description               Text,
  points                    float,
  additional                boolean,
  assignment_id             bigint,
  constraint pk_subtasks primary key (id))
;

create table token (
  id                        bigint not null,
  token                     varchar(255),
  target_user_id            bigint,
  type                      varchar(2),
  created                   timestamp,
  expires                   timestamp,
  constraint ck_token_type check (type in ('EV','PR')),
  constraint uq_token_token unique (token),
  constraint pk_token primary key (id))
;

create table users (
  id                        bigint not null,
  email                     varchar(255),
  first_name                varchar(255) not null,
  last_name                 varchar(255) not null,
  password                  varchar(255) not null,
  matrikel_number           varchar(255),
  last_login                timestamp,
  email_validated           boolean,
  role_name                 varchar(255),
  constraint uq_users_email unique (email),
  constraint uq_users_matrikel_number unique (matrikel_number),
  constraint pk_users primary key (id))
;

create table valuations (
  id                        bigint not null,
  resultpoints              float,
  comment                   varchar(255),
  corrected                 boolean,
  assessment_id             bigint,
  subtask_id                bigint,
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
create sequence assessments_seq;

create sequence assignments_seq;

create sequence dutys_seq;

create sequence lectures_seq;

create sequence roles_seq;

create sequence semesters_seq;

create sequence stuff_seq;

create sequence subtasks_seq;

create sequence token_seq;

create sequence users_seq;

create sequence valuations_seq;

alter table assessments add constraint fk_assessments_duty_1 foreign key (duty_id) references dutys (id) on delete restrict on update restrict;
create index ix_assessments_duty_1 on assessments (duty_id);
alter table assessments add constraint fk_assessments_proofreader_2 foreign key (proofreader_id) references users (id) on delete restrict on update restrict;
create index ix_assessments_proofreader_2 on assessments (proofreader_id);
alter table assignments add constraint fk_assignments_lecture_3 foreign key (lecture_id) references lectures (id) on delete restrict on update restrict;
create index ix_assignments_lecture_3 on assignments (lecture_id);
alter table dutys add constraint fk_dutys_assignment_4 foreign key (assignment_id) references assignments (id) on delete restrict on update restrict;
create index ix_dutys_assignment_4 on dutys (assignment_id);
alter table dutys add constraint fk_dutys_assessment_5 foreign key (assessment_id) references assessments (id) on delete restrict on update restrict;
create index ix_dutys_assessment_5 on dutys (assessment_id);
alter table dutys add constraint fk_dutys_user_6 foreign key (user_id) references users (id) on delete restrict on update restrict;
create index ix_dutys_user_6 on dutys (user_id);
alter table lectures add constraint fk_lectures_semester_7 foreign key (semester_id) references semesters (id) on delete restrict on update restrict;
create index ix_lectures_semester_7 on lectures (semester_id);
alter table stuff add constraint fk_stuff_assignment_8 foreign key (assignment_id) references assignments (id) on delete restrict on update restrict;
create index ix_stuff_assignment_8 on stuff (assignment_id);
alter table subtasks add constraint fk_subtasks_assignment_9 foreign key (assignment_id) references assignments (id) on delete restrict on update restrict;
create index ix_subtasks_assignment_9 on subtasks (assignment_id);
alter table users add constraint fk_users_role_10 foreign key (role_name) references roles (name) on delete restrict on update restrict;
create index ix_users_role_10 on users (role_name);
alter table valuations add constraint fk_valuations_assessment_11 foreign key (assessment_id) references assessments (id) on delete restrict on update restrict;
create index ix_valuations_assessment_11 on valuations (assessment_id);
alter table valuations add constraint fk_valuations_subtask_12 foreign key (subtask_id) references subtasks (id) on delete restrict on update restrict;
create index ix_valuations_subtask_12 on valuations (subtask_id);



alter table users_lectures add constraint fk_users_lectures_lectures_01 foreign key (lectures_id) references lectures (id) on delete restrict on update restrict;

alter table users_lectures add constraint fk_users_lectures_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_lectureadmins add constraint fk_users_lectureadmins_lectur_01 foreign key (lectures_id) references lectures (id) on delete restrict on update restrict;

alter table users_lectureadmins add constraint fk_users_lectureadmins_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

alter table users_proofreaders add constraint fk_users_proofreaders_lecture_01 foreign key (lectures_id) references lectures (id) on delete restrict on update restrict;

alter table users_proofreaders add constraint fk_users_proofreaders_users_02 foreign key (users_id) references users (id) on delete restrict on update restrict;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists assessments;

drop table if exists assignments;

drop table if exists dutys;

drop table if exists lectures;

drop table if exists users_lectures;

drop table if exists users_lectureadmins;

drop table if exists users_proofreaders;

drop table if exists roles;

drop table if exists semesters;

drop table if exists stuff;

drop table if exists subtasks;

drop table if exists token;

drop table if exists users;

drop table if exists valuations;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists assessments_seq;

drop sequence if exists assignments_seq;

drop sequence if exists dutys_seq;

drop sequence if exists lectures_seq;

drop sequence if exists roles_seq;

drop sequence if exists semesters_seq;

drop sequence if exists stuff_seq;

drop sequence if exists subtasks_seq;

drop sequence if exists token_seq;

drop sequence if exists users_seq;

drop sequence if exists valuations_seq;

