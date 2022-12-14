package dev.mdalvz.trashdb.accessor;

import java.io.IOException;

public class DecoratorAccessor implements Accessor {

    private final Accessor base;

    public DecoratorAccessor(Accessor base) {
        this.base = base;
    }

    @Override
    public void read(long srcOffset, int size, int dstOffset, byte[] dst) throws IOException {
        base.read(srcOffset, size, dstOffset, dst);
    }

    @Override
    public void write(long dstOffset, int size, int srcOffset, byte[] src) throws IOException {
        base.write(dstOffset, size, srcOffset, src);
    }

    @Override
    public long getSize() throws IOException {
        return base.getSize();
    }

    @Override
    public void setSize(long size) throws IOException {
        base.setSize(size);
    }

    @Override
    public void flush() throws IOException {
        base.flush();
    }

}
