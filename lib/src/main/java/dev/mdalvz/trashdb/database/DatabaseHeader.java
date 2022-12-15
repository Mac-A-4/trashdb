package dev.mdalvz.trashdb.database;

import dev.mdalvz.trashdb.serializer.Serializer;
import dev.mdalvz.trashdb.serializer.Struct;

import java.io.IOException;

public class DatabaseHeader implements Struct {

    public static final int SIZE = Serializer.LONG_SIZE * 7;
    public static final int TABLE_OFFSET_OFFSET = 0;
    public static final int TABLE_SIZE_OFFSET = TABLE_OFFSET_OFFSET + Serializer.LONG_SIZE;
    public static final int ROOT_OFFSET = TABLE_SIZE_OFFSET + Serializer.LONG_SIZE;
    public static final int HEAP_OFFSET_OFFSET = ROOT_OFFSET + Serializer.LONG_SIZE;
    public static final int HEAP_SIZE_OFFSET = HEAP_OFFSET_OFFSET + Serializer.LONG_SIZE;
    public static final int HEAP_FRONT_OFFSET = HEAP_SIZE_OFFSET + Serializer.LONG_SIZE;
    public static final int HEAP_BACK_OFFSET = HEAP_FRONT_OFFSET + Serializer.LONG_SIZE;
    private long tableOffset;
    private long tableSize;
    private long root;
    private long heapOffset;
    private long heapSize;
    private long heapFront;
    private long heapBack;

    @Override
    public void read(Serializer serializer, long srcOffset) throws IOException {
        setTableOffset(serializer.readLong(srcOffset + TABLE_OFFSET_OFFSET));
        setTableSize(serializer.readLong(srcOffset + TABLE_SIZE_OFFSET));
        setRoot(serializer.readLong(srcOffset + ROOT_OFFSET));
        setHeapOffset(serializer.readLong(srcOffset + HEAP_OFFSET_OFFSET));
        setHeapSize(serializer.readLong(srcOffset + HEAP_SIZE_OFFSET));
        setHeapFront(serializer.readLong(srcOffset + HEAP_FRONT_OFFSET));
        setHeapBack(serializer.readLong(srcOffset + HEAP_BACK_OFFSET));
    }

    @Override
    public void write(Serializer serializer, long dstOffset) throws IOException {
        serializer.writeLong(dstOffset + TABLE_OFFSET_OFFSET, getTableOffset());
        serializer.writeLong(dstOffset + TABLE_SIZE_OFFSET, getTableSize());
        serializer.writeLong(dstOffset + ROOT_OFFSET, getRoot());
        serializer.writeLong(dstOffset + HEAP_OFFSET_OFFSET, getHeapOffset());
        serializer.writeLong(dstOffset + HEAP_SIZE_OFFSET, getHeapSize());
        serializer.writeLong(dstOffset + HEAP_FRONT_OFFSET, getHeapFront());
        serializer.writeLong(dstOffset + HEAP_BACK_OFFSET, getHeapBack());
    }

    public long getTableOffset() {
        return tableOffset;
    }

    public void setTableOffset(long tableOffset) {
        this.tableOffset = tableOffset;
    }

    public long getTableSize() {
        return tableSize;
    }

    public void setTableSize(long tableSize) {
        this.tableSize = tableSize;
    }

    public long getRoot() {
        return root;
    }

    public void setRoot(long root) {
        this.root = root;
    }

    public long getHeapOffset() {
        return heapOffset;
    }

    public void setHeapOffset(long heapOffset) {
        this.heapOffset = heapOffset;
    }

    public long getHeapSize() {
        return heapSize;
    }

    public void setHeapSize(long heapSize) {
        this.heapSize = heapSize;
    }

    public long getHeapFront() {
        return heapFront;
    }

    public void setHeapFront(long heapFront) {
        this.heapFront = heapFront;
    }

    public long getHeapBack() {
        return heapBack;
    }

    public void setHeapBack(long heapBack) {
        this.heapBack = heapBack;
    }

}
