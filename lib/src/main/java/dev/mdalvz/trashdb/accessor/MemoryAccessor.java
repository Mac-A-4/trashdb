package dev.mdalvz.trashdb.accessor;

public class MemoryAccessor implements Accessor {

    private byte[] data;

    public MemoryAccessor() {
        data = new byte[0];
    }

    public MemoryAccessor(int size) {
        if (size % GRANULARITY != 0) {
            throw new IllegalArgumentException("size passed to MemoryAccessor has invalid granularity");
        }
        data = new byte[size];
    }

    @Override
    public void read(long srcOffset, int size, int dstOffset, byte[] dst) {
        System.arraycopy(data, (int)srcOffset, dst, dstOffset, size);
    }

    @Override
    public void write(long dstOffset, int size, int srcOffset, byte[] src) {
        System.arraycopy(src, srcOffset, data, (int)dstOffset, size);
    }

    @Override
    public long getSize() {
        return data.length;
    }

    @Override
    public void setSize(long size) {
        if (size % GRANULARITY != 0) {
            throw new IllegalArgumentException("size passed to MemoryAccessor.setSize has invalid granularity");
        }
        byte[] result = new byte[(int)size];
        int copySize = Math.min(result.length, data.length);
        System.arraycopy(data, 0, result, 0, copySize);
        data = result;
    }

    @Override
    public void flush() {}

}
