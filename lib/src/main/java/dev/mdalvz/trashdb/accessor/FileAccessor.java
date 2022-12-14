package dev.mdalvz.trashdb.accessor;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class FileAccessor implements Accessor {

    private final FileChannel fileChannel;

    public FileAccessor(FileChannel fileChannel) throws IOException {
        if (fileChannel.size() % GRANULARITY != 0) {
            throw new IllegalArgumentException("FileChannel passed to FileAccessor has invalid granularity");
        }
        this.fileChannel = fileChannel;
    }

    @Override
    public void read(long srcOffset, int size, int dstOffset, byte[] dst) throws IOException {
        final ByteBuffer dstBuffer = ByteBuffer.wrap(dst, dstOffset, size);
        fileChannel.read(dstBuffer, srcOffset);
    }

    @Override
    public void write(long dstOffset, int size, int srcOffset, byte[] src) throws IOException {
        final ByteBuffer srcBuffer = ByteBuffer.wrap(src, srcOffset, size);
        fileChannel.write(srcBuffer, dstOffset);
    }

    @Override
    public long getSize() throws IOException {
        return fileChannel.size();
    }

    @Override
    public void setSize(long size) throws IOException {
        if (size % GRANULARITY != 0) {
            throw new IllegalArgumentException("size passed to FileAccessor.setSize has invalid granularity");
        }
        fileChannel.truncate(size);
    }

    @Override
    public void flush() throws IOException {
        fileChannel.force(true);
    }

}
