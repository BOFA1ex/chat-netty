package com.bofa.attribute;

import java.util.Arrays;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.attribute
 * @date 2019/4/9
 */
public enum UserStatus {

    /**
     *
     */
    ONLINE(1, "在线"),
    OFFLINE(2, "离线"),
    VISIBLE(3, "隐身");

    public int status;

    public String comment;

    UserStatus(int status, String comment) {
        this.status = status;
        this.comment = comment;
    }

    public static String findByStatus(int status){
        for (UserStatus us : UserStatus.values()) {
            if (us.status == status) {
                return us.comment;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(Arrays.toString(UserStatus.values()));
    }

    @Override
    public String toString() {
        return status + ": " + comment;
    }
}
