# --- !Ups

create table dakoku (
  id  integer  auto_increment primary key,
  employee_id  integer not null,
  start_work_at  datetime not null,
  finish_work_at  datetime
);


# --- !Downs

drop table if exists dakoku;
