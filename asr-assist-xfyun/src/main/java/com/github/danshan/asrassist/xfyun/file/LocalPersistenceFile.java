package com.github.danshan.asrassist.xfyun.file;

import com.github.danshan.asrassist.xfyun.exception.LfasrException;
import com.github.danshan.asrassist.xfyun.model.ErrorCode;
import com.github.danshan.asrassist.xfyun.model.Message;

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
        } catch (IOException ex) {
            throw new LfasrException(Message.failed(ErrorCode.ASR_BREAKPOINT_PERSISTENCE_ERR, null));
        } finally {
            if (randomFile != null) {
                try {
                    randomFile.close();
                } catch (IOException ex) {
                    throw new LfasrException(Message.failed(ErrorCode.ASR_BREAKPOINT_PERSISTENCE_ERR, null));
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
        } catch (Exception ex) {
            throw new LfasrException(Message.failed(ErrorCode.ASR_BREAKPOINT_FOLDER_ERR, null));
        }
    }
}
