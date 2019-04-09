package com.bofa.exception;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.exception
 * @date 2019/4/4
 */
/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.myblog.management.exception
 * @date 2019/4/3
 */
public class ChatException extends RuntimeException {

    private static final long serialVersionUID = 4242111829918405178L;

    public ChatErrorCode errorCode;

    public ChatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ChatException(ChatErrorCode errorCode, String... message) {
        super(errorCode.getMessageAndCompletion(message));
        this.errorCode = errorCode;
    }

    public ChatException(String message) {
        super(message);
    }

    public static void throwChatException(ChatErrorCode errorCode, String... message){
        throw new ChatException(errorCode, message);
    }

    public static void throwChatException(String message){
        throw new ChatException(message);
    }


    public static void throwChatException(String message, Throwable cause){
        throw new ChatException(message, cause);
    }

}

