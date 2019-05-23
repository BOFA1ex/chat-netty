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
     * 1:好友离线信息
     * 2:好友验证信息
     * 3:异地登录信息
     * 4:群聊信息
     * 5:好友信息
     */
    FRIEND_OFFLINE_MESSAGE(1, "好友离线"),
    FRIEND_APPROVAL_MESSAGE(2, "好友验证"),
    FRIEND_APPROVAL_MESSAGE_CALLBACK(3, "好友验证回调"),
    OTHER_PLACE_LOGIN(4, "异地登录"),
    GROUP_OFFLINE_MESSAGE(5, "群聊离线"),
    FRIEND_UNREAD_MESSAGE(6, "好友信息");

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
