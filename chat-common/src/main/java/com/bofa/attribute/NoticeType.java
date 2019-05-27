package com.bofa.attribute;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.attribute
 * @date 2019/4/16
 */
public enum NoticeType {

    /**
     * 通知类型
     */

    FRIEND_UNREAD_MESSAGE(1, "好友未读消息"),
    FRIEND_APPROVAL_MESSAGE(2, "好友验证请求"),
    FRIEND_APPROVAL_MESSAGE_CALLBACK(3, "好友验证响应"),
    OTHER_PLACE_LOGIN(4, "异地登录"),
    GROUP_OFFLINE_MESSAGE(5, "群聊离线"),
    INTERNAL_SYSTEM_ERROR(6, "系统异常");

    public Integer type;
    public String comment;

    NoticeType(Integer type, String comment) {
        this.type = type;
        this.comment = comment;
    }

    public static String findByType(int type){
        for (NoticeType nt : NoticeType.values()) {
            if (nt.type == type) {
                return nt.comment;
            }
        }
        return null;
    }
}
