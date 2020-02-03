package com.github.danshan.asrassist.xfyun.event;

public enum EventType {
    DATA_CONTENT(1, "文件数据内容"),
    DATA_END(2, "文件结束标志");

    public final int value;
    public final String name;

    EventType(int value, String name) {
        this.value = value;
        this.name = name;
    }

}
