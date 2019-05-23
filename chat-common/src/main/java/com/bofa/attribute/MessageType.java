package com.bofa.attribute;

import java.util.stream.Stream;

/**
 * @author Bofa
 * @version 1.0
 * @decription com.bofa.attribute
 * @date 2019/4/13
 */
public enum MessageType {
    /**
     *
     */
    TEXT(1, "文字"),
    PIC(2, "图片"),
    VOICE(3, "语音"),
    VIDEO(4, "音频");

    public int type;
    public String comment;

    MessageType(int type, String comment) {
        this.type = type;
        this.comment = comment;
    }

    public static String getCommentByType(int type) {
        return Stream.of(MessageType.values()).filter(messageType -> messageType.type == type)
                .findFirst().orElse(MessageType.TEXT).comment;
    }
}
