package dev.mdalvz.trashdb.journal;

import dev.mdalvz.trashdb.struct.Serializer;
import dev.mdalvz.trashdb.struct.Struct;

import java.io.IOException;

public class JournalHeader implements Struct {

    public static final int SIZE = Serializer.BOOLEAN_SIZE + Serializer.LONG_SIZE + Serializer.LONG_SIZE;
    public static final int ACTIVE_OFFSET = 0;
    public static final int ORIGINAL_SIZE_OFFSET = ACTIVE_OFFSET + Serializer.BOOLEAN_SIZE;
    public static final int ENTRY_COUNT_OFFSET = ORIGINAL_SIZE_OFFSET + Serializer.LONG_SIZE;
    private boolean active;
    private long originalSize;
    private long entryCount;

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(long originalSize) {
        this.originalSize = originalSize;
    }

    public long getEntryCount() {
        return entryCount;
    }

    public void setEntryCount(long entryCount) {
        this.entryCount = entryCount;
    }

    @Override
    public void read(Serializer serializer, long srcOffset) throws IOException {
        setActive(serializer.readBoolean(srcOffset + ACTIVE_OFFSET));
        setOriginalSize(serializer.readLong(srcOffset + ORIGINAL_SIZE_OFFSET));
        setEntryCount(serializer.readLong(srcOffset + ENTRY_COUNT_OFFSET));
    }

    @Override
    public void write(Serializer serializer, long dstOffset) throws IOException {
        serializer.writeBoolean(dstOffset + ACTIVE_OFFSET, isActive());
        serializer.writeLong(dstOffset + ORIGINAL_SIZE_OFFSET, getOriginalSize());
        serializer.writeLong(dstOffset + ENTRY_COUNT_OFFSET, getEntryCount());
    }

}
