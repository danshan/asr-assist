package com.github.danshan.asrassist.xfyun.model;

public enum EventType {
    LFASR_FILE_DATA_CONTENT("文件数据内容", 0),
    LFASR_FILE_DATA_END("文件结束标志", 1);

    public final String name;
    public final int value;

    EventType(String name, int value) {
        this.name = name;
        this.value = value;
    }

}
