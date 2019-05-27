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

    /**
     * 根据状态值返回说明(用于friend -l指令)
     * 不能反馈用户隐身的状态, 因此需要判断反馈为离线状态.
     * @param status
     * @param itself difference by status -l and friend -l
     * @return
     */
    public static String findByStatus(int status, boolean itself){
        for (UserStatus us : UserStatus.values()) {
            if (us.status == status) {
                if (!itself && status == VISIBLE.status){
                    return OFFLINE.comment;
                }
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
