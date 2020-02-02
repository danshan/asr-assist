package com.github.danshan.asrassist.xfyun.exception;

import com.github.danshan.asrassist.xfyun.model.ErrorMsg;

public class SliceException extends LfasrException {
    private static final long serialVersionUID = 5032944132358207531L;

    public SliceException(ErrorMsg errorMsg) {
        super(errorMsg);
    }
}
