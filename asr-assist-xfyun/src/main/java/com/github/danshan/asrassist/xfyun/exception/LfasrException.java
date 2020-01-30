package com.github.danshan.asrassist.xfyun.exception;

import com.github.danshan.asrassist.xfyun.model.Message;

public class LfasrException extends Exception {
    private static final long serialVersionUID = -7752193260703835881L;

    private Message message;

    public LfasrException(Message message) {
        super(message.getFailed());
        this.message = message;
    }

}
