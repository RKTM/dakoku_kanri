# --- !Ups

alter table dakoku
  add column start_work_geo point
, add column finish_work_geo point
;


# --- !Downs

alter table drop column start_work_geo, finish_work_geo;

