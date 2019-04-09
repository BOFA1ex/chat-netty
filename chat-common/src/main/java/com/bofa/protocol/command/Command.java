package com.bofa.protocol.command;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.protocol.command
 * @date 2019/4/2
 */
public enum Command {

    /**
     * LOGIN_REQUEST 登陆请求
     * LOGIN_RESPONSE 登陆响应
     * REGISTER_REQUEST 注册请求
     * REGISTER_RESPONSE 注册响应
     * CHANGE_STATUS_REQUEST 修改当前状态
     * CHANGE_STATUS_RESPONSE 修改当前状态
     * FIND_FRIEND_REQUEST 查看用户组请求(分为活跃和非活跃用户,ps：隐身和离线是非活跃)
     * FIND_FRIEND_RESPONSE 查看用户组响应
     * CREATE_GROUP_REQUEST 创建群聊请求
     * CREATE_GROUP_RESPONSE 创建群聊请求
     * MESSAGE_REQUEST 消息请求
     * MESSAGE_RESPONSE 消息响应
     */
    NULL((byte) 0, "/"),
    LOGIN_REQUEST((byte) 1, "/chat/user/login"),
    LOGIN_RESPONSE((byte) -1, "/chat/user/login"),
    LOGOUT_REQUEST((byte) 2, "/chat/user/changeStatus"),
    LOGOUT_RESPONSE((byte) -2, "/chat/user/changeStatus"),
    REGISTER_REQUEST((byte) 3, "/chat/user/register"),
    REGISTER_RESPONSE((byte) -3, "/chat/user/register"),
    CHANGE_STATUS_REQUEST((byte) 4, "/chat/user/changeStatus"),
    CHANGE_STATUS_RESPONSE((byte) -4, "/chat/user/changeStatus"),
    MESSAGE_REQUEST((byte) 5, "/chat/user/message"),
    MESSAGE_RESPONSE((byte) -5, "/chat/user/message");

    public byte command;

    public String url;

    Command(byte command, String url) {
        this.command = command;
        this.url = url;
    }


}
