# --- !Ups

create table dakoku (
  id  serial primary key,
  employee_id  integer not null,
  start_work_at  timestamp not null,
  finish_work_at  timestamp
);


# --- !Downs

drop table if exists dakoku;
