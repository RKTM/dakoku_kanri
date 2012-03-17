# --- !Ups

create table employee (
  id  serial primary key,
  employee_cd  varchar(255) unique not null,
  name  varchar(255) not null
);


# --- !Downs

drop table if exists employee;
