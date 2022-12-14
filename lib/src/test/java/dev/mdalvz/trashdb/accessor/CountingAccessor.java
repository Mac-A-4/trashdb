package dev.mdalvz.trashdb.accessor;

import java.io.IOException;

public class CountingAccessor extends DecoratorAccessor {

    private int readCount;
    private int writeCount;

    public CountingAccessor(Accessor base) {
        super(base);
    }

    @Override
    public void read(long srcOffset, int size, int dstOffset, byte[] dst) throws IOException {
        super.read(srcOffset, size, dstOffset, dst);
        readCount++;
    }

    @Override
    public void write(long dstOffset, int size, int srcOffset, byte[] src) throws IOException {
        super.write(dstOffset, size, srcOffset, src);
        writeCount++;
    }

    public int getReadCount() {
        return readCount;
    }

    public int getWriteCount() {
        return writeCount;
    }

}
