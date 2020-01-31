package com.github.danshan.asrassist.xfyun.util;

public class DictUtil {
    private int length = 0;
    private char[] ch;

    public DictUtil(String str) {
        this.length = str.length();
        this.ch = str.toCharArray();
    }

    public String getNextString() {
        int i = 0;

        for(int j = this.length - 1; i < this.length && j >= 0; ++i) {
            if (this.ch[j] != 'z') {
                ++this.ch[j];
                break;
            }

            this.ch[j] = 'a';
            --j;
        }

        return new String(this.ch);
    }

}
