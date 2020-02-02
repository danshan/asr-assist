package com.github.danshan.asrassist.xfyun.exception;

import com.github.danshan.asrassist.xfyun.model.ErrorMsg;
import lombok.Getter;

public class LfasrException extends RuntimeException {
    private static final long serialVersionUID = -7752193260703835881L;

    @Getter
    private ErrorMsg errorMsg;

    public LfasrException(ErrorMsg errorMsg) {
        super(errorMsg.getFailed());
        this.errorMsg = errorMsg;
    }

}
