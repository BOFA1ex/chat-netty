-- auto-generated definition
create table USER
(
  USERID   INTEGER      not null
    primary key,
  USERNAME VARCHAR(256) not null
  constraint USER_USERNAME_UINDEX
  unique
);

comment on table USER
is '上一次登录的用户信息(只存单条记录)';

create unique index USER_USERID_UINDEX
  on USER (USERID);

