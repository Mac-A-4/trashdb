package dev.mdalvz.trashdb.accessor;

public class ReadAccessor extends DecoratorAccessor {

    public ReadAccessor(Accessor base) {
        super(base);
    }

    @Override
    public void write(long dstOffset, int size, int srcOffset, byte[] src) {
        throw new IllegalStateException("Attempted to perform ReadAccessor.write");
    }

    @Override
    public void setSize(long size) {
        throw new IllegalStateException("Attempted to perform ReadAccessor.setSize");
    }

    @Override
    public void flush() {
        throw new IllegalStateException("Attempted to perform ReadAccessor.flush");
    }

}
