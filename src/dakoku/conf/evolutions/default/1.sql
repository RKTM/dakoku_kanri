# --- !Ups

create table employee (
  id  integer  auto_increment primary key,
  employee_cd  varchar(255) not null,
  name  varchar(255) not null
);


# --- !Downs

drop table if exists employee;
