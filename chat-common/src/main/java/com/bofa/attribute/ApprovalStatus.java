package com.bofa.attribute;

/**
 * @author Bofa
 * @version 1.0
 * @description com.bofa.attribute
 * @date 2019/4/28
 */
public interface ApprovalStatus {
    /**
     * 1:申请中
     * 2:申请成功
     * 3:申请失败
     */
    int APPLYING = 1;
    int APPLY_SC = 2;
    int APPLY_FA = 3;
}
