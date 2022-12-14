package dev.mdalvz.trashdb.journal;

import dev.mdalvz.trashdb.accessor.Accessor;
import dev.mdalvz.trashdb.struct.Serializer;
import dev.mdalvz.trashdb.struct.Struct;

import java.io.IOException;

public class JournalEntry implements Struct {

    public static final int DATA_SIZE = Accessor.GRANULARITY;
    public static final int SIZE = Serializer.LONG_SIZE + DATA_SIZE;
    public static final int OFFSET_OFFSET = 0;
    public static final int DATA_OFFSET = OFFSET_OFFSET + Serializer.LONG_SIZE;
    private long offset;
    private final byte[] data = new byte[DATA_SIZE];

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void read(Serializer serializer, long srcOffset) throws IOException {
        setOffset(serializer.readLong(srcOffset + OFFSET_OFFSET));
        serializer.read(srcOffset + DATA_OFFSET, DATA_SIZE, 0, getData());
    }

    @Override
    public void write(Serializer serializer, long dstOffset) throws IOException {
        serializer.writeLong(dstOffset + OFFSET_OFFSET, getOffset());
        serializer.write(dstOffset + DATA_OFFSET, DATA_SIZE, 0, getData());
    }

}
