package dev.mdalvz.trashdb.database;

import dev.mdalvz.trashdb.serializer.Serializer;
import dev.mdalvz.trashdb.serializer.Struct;

import java.io.IOException;

public class DatabaseObjectHeader implements Struct {

    public static final int SIZE = Serializer.LONG_SIZE * 4;
    public static final int TYPE_OFFSET = 0;
    public static final int SIZE_OFFSET = TYPE_OFFSET + Serializer.LONG_SIZE;
    public static final int BACKWARD_OFFSET = SIZE_OFFSET + Serializer.LONG_SIZE;
    public static final int FORWARD_OFFSET = BACKWARD_OFFSET + Serializer.LONG_SIZE;
    private long type;
    private long size;
    private long backward;
    private long forward;

    @Override
    public void read(Serializer serializer, long srcOffset) throws IOException {
        setType(serializer.readLong(srcOffset + TYPE_OFFSET));
        setSize(serializer.readLong(srcOffset + SIZE_OFFSET));
        setBackward(serializer.readLong(srcOffset + BACKWARD_OFFSET));
        setForward(serializer.readLong(srcOffset + FORWARD_OFFSET));
    }

    @Override
    public void write(Serializer serializer, long dstOffset) throws IOException {
        serializer.writeLong(dstOffset + TYPE_OFFSET, getType());
        serializer.writeLong(dstOffset + SIZE_OFFSET, getSize());
        serializer.writeLong(dstOffset + BACKWARD_OFFSET, getBackward());
        serializer.writeLong(dstOffset + FORWARD_OFFSET, getForward());
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getBackward() {
        return backward;
    }

    public void setBackward(long backward) {
        this.backward = backward;
    }

    public long getForward() {
        return forward;
    }

    public void setForward(long forward) {
        this.forward = forward;
    }

}
