package com.github.danshan.asrassist.xfyun.file;

import com.github.danshan.asrassist.xfyun.exception.LfasrException;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class LocalPersistenceFile {
    private LocalPersistenceFile() {
    }

    public static void writeNIO(String filename, String content) throws LfasrException {
        RandomAccessFile randomFile = null;

        try {
            randomFile = new RandomAccessFile(filename, "rw");
            long fileLength = randomFile.length();
            randomFile.seek(fileLength);
            randomFile.writeBytes(content + "\r\n");
        } catch (IOException var12) {
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26301\", \"failed\":\"转写断点续传持久化文件读写错误!\", \"data\":\"\"}");
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException var11) {
                    throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26301\", \"failed\":\"转写断点续传持久化文件读写错误!\", \"data\":\"\"}");
                }
            }

        }

    }

    public static boolean deleteFile(File file) {
        boolean flag = false;
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }

        return flag;
    }

    public static List<File> getFileList(String path) throws LfasrException {
        ArrayList filelist = new ArrayList();

        try {
            File dir = new File(path);
            File[] files = dir.listFiles();
            if (files != null) {
                for(int i = 0; i < files.length; ++i) {
                    String fileName = files[i].getName();
                    if (files[i].isDirectory()) {
                        getFileList(files[i].getAbsolutePath());
                    } else if (fileName.endsWith("dat")) {
                        filelist.add(files[i]);
                    }
                }
            }

            return filelist;
        } catch (Exception var6) {
            throw new LfasrException("{\"ok\":\"-1\", \"err_no\":\"26302\", \"failed\":\"转写断点续传文件夹读写错误!\", \"data\":\"\"}");
        }
    }
}
