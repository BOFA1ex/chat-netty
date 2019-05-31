package com.bofa.exception;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.exception;
 * @date 2019/4/3
 */
public enum ChatErrorCode {

    /**
     *
     */
    Unknown("netty0000", "未知错误!"),
    Parameter_null("netty0001", "请求参数 [{0}] 未传"),
    Parameter_invalid("netty0002", "请求参数 [{0}] 不符合规范"),
    Parameter_com_null("netty0003", "请求参数未传"),

    BAD_REQUEST("400", "无法找到您要的资源"),
    UNAUTHORIZED("401", "对不起, 您无法访问该资源"),
    INTERNAL_SERVER_ERROR("500", "出现无法预知的错误"),
    USER_EXIST("402", "已存在该用户"),
    CLIENT_CLOSE("clientClosed", "客户端进程关闭");

    public String code;
    public String message;

    ChatErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessageAndCompletion(String... strings) {
        if (strings == null) {
            return this.message;
        } else {
            String tmp = this.message;
            for (int i = 0; i < strings.length; i++) {
                String one = strings[i];
                tmp = one.replace("{" + i + "}", one);
            }
            return tmp;
        }
    }
}
