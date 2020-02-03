package com.github.danshan.asrassist.xfyun.file;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Getter
@RequiredArgsConstructor
public class ChannelFileReader implements Closeable {

    private final FileInputStream fileIn;
    private final ByteBuffer byteBuf;
    private final long fileLength;
    private final String fileExt;

    private byte[] array;

    public ChannelFileReader(File file, int bufferSize) throws IOException {
        this.fileIn = new FileInputStream(file.getCanonicalPath());
        this.fileLength = this.fileIn.getChannel().size();
        this.byteBuf = ByteBuffer.allocate(bufferSize);
        this.fileExt = file.getName().substring(file.getName().lastIndexOf('.') + 1);
    }

    public int read() throws IOException {
        FileChannel fileChannel = this.fileIn.getChannel();
        int bytes = fileChannel.read(this.byteBuf);
        if (bytes != -1) {
            this.array = new byte[bytes];
            this.byteBuf.flip();
            this.byteBuf.get(this.array);
            this.byteBuf.clear();
            return bytes;
        } else {
            return -1;
        }
    }

    public void close() throws IOException {
        if (this.fileIn != null) {
            this.fileIn.close();
        }

        this.array = null;
    }

}
