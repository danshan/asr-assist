package com.github.danshan.asrassist.xfyun.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class FileSlice {
    private final String sliceId;
    private final byte[] body;
}
