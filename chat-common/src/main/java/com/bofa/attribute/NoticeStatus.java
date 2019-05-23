package com.bofa.attribute;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.attribute
 * @date 2019/4/16
 */
public enum NoticeStatus {
    /**
     * 通知标记
     * 1: 已读
     * 2: 未读
     */
    READ(1, "已读"),
    UNREAD(2, "未读");

    public Integer status;

    public String comment;

    NoticeStatus(Integer status, String comment) {
        this.status = status;
        this.comment = comment;
    }

    public static String findByStatus(int status){
        for (NoticeStatus ns : NoticeStatus.values()) {
            if (ns.status == status) {
                return ns.comment;
            }
        }
        return null;
    }
}
