create table USER_NOTICE
(
	ID             VARCHAR(64)      not null
		primary key,
	USERID         INTEGER      not null,
	USERNAME       VARCHAR(256) not null,
	NOTICEID       INTEGER      not null,
	NOTICENAME     VARCHAR(256) not null,
	NOTICESTATUS   INTEGER      not null,
	NOTICEDATETIME VARCHAR(256)         not null,
	NOTICETYPE     INTEGER,
	NOTICECONTENT  VARCHAR(256) not null,
);

comment on table USER_NOTICE
is '客户端通知缓存';

comment on column USER_NOTICE.NOTICENAME
is '如果是好友离线信息或者好友验证信息
则为好友用户名
如果是异地通知则为System
群聊信息则为群聊房间名';

comment on column USER_NOTICE.NOTICESTATUS
is '1: 已读
2: 未读';

comment on column USER_NOTICE.NOTICETYPE
is '1:好友离线信息
2:好友验证信息
3:异地登录信息
4:群聊信息';

create unique index USER_NOTICE_ID_UINDEX
	on USER_NOTICE (ID);