create table USER_MESSAGE
(
	MESSAGEID    VARCHAR(64)       not null
		primary key,
	FROMUSERNAME VARCHAR(256)  not null,
	TOUSERNAME   VARCHAR(256)  not null,
	TOUSERID     INTEGER       not null,
	FROMUSERID   INTEGER       not null,
	MESSAGETYPE  INTEGER       not null,
	DATETIME     DATETIME          not null,
	CONTENT      VARCHAR(2048) not null
);

comment on table USER_MESSAGE
is '客户端单聊信息缓存';

comment on column USER_MESSAGE.MESSAGETYPE
is '信息类型
1:文字
2:图片
3:视频
4:语音';

create unique index USER_MESSAGE_MESSAGEID_UINDEX
	on USER_MESSAGE (MESSAGEID);
