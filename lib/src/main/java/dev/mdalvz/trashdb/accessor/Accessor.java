package dev.mdalvz.trashdb.accessor;

import java.io.IOException;

public interface Accessor {

    int GRANULARITY = 0x1000;

    void read(long srcOffset, int size, int dstOffset, byte[] dst) throws IOException;

    void write(long dstOffset, int size, int srcOffset, byte[] src) throws IOException;

    long getSize() throws IOException;

    void setSize(long size) throws IOException;

    void flush() throws IOException;

}
