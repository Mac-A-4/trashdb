package dev.mdalvz.trashdb.cache;

import dev.mdalvz.trashdb.accessor.Accessor;
import dev.mdalvz.trashdb.accessor.DecoratorAccessor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class CacheAccessor extends DecoratorAccessor {

    private final CacheQueue activeQueue;
    private final CacheQueue inactiveQueue;
    private final HashMap<Long, CacheEntry> activeMap;

    public CacheAccessor(Accessor base, int capacity) {
        super(base);
        activeQueue = new CacheQueue();
        activeMap = new HashMap<>();
        inactiveQueue = new CacheQueue();
        for (int i = 0; i < capacity; ++i) {
            inactiveQueue.add(new CacheEntry());
        }
    }

    private void readEntry(CacheEntry entry, long offset) throws IOException {
        if (getSize() - offset < GRANULARITY) {
            throw new IndexOutOfBoundsException();
        }
        super.read(offset, GRANULARITY, 0, entry.getData());
        entry.setOffset(offset);
        entry.setModified(false);
    }

    private void writeEntry(CacheEntry entry) throws IOException {
        if (!entry.isModified() || getSize() - entry.getOffset() < GRANULARITY) {
            return;
        }
        super.write(entry.getOffset(), GRANULARITY, 0, entry.getData());
        entry.setModified(false);
    }

    private void deactivateEntry(CacheEntry entry) {
        entry.setModified(false);
        entry.setOffset(0);
        inactiveQueue.add(entry);
    }

    private void evictEntry(CacheEntry entry) throws IOException {
        activeQueue.remove(entry);
        activeMap.remove(entry.getOffset());
        try {
            writeEntry(entry);
        } finally {
            deactivateEntry(entry);
        }
    }

    private CacheEntry getEntry(long offset) throws IOException {
        if (activeMap.containsKey(offset)) {
            final CacheEntry result = activeMap.get(offset);
            activeQueue.promote(result);
            return result;
        }
        if (inactiveQueue.isEmpty()) {
            evictEntry(activeQueue.getFront());
        }
        final CacheEntry result = inactiveQueue.getFront();
        inactiveQueue.remove(result);
        try {
            readEntry(result, offset);
        } catch (IOException | IndexOutOfBoundsException e) {
            deactivateEntry(result);
            throw e;
        }
        activeQueue.add(result);
        activeMap.put(offset, result);
        return result;
    }

    private void invalidate() throws IOException {
        final long size = getSize();
        final List<CacheEntry> entries = activeMap.values().stream()
                .filter(entry -> entry.getOffset() + GRANULARITY > size)
                .toList();

        for (CacheEntry entry : entries) {
            evictEntry(entry);
        }
    }

    @Override
    public void read(long srcOffset, int size, int dstOffset, byte[] dst) throws IOException {
        while (size > 0) {
            final long baseMod = srcOffset % GRANULARITY;
            final long baseOffset = srcOffset - baseMod;
            final CacheEntry entry = getEntry(baseOffset);
            final int readSize = Math.min(GRANULARITY - (int)baseMod, size);
            System.arraycopy(entry.getData(), (int)baseMod, dst, dstOffset, readSize);
            srcOffset += readSize;
            dstOffset += readSize;
            size -= readSize;
        }
    }

    @Override
    public void write(long dstOffset, int size, int srcOffset, byte[] src) throws IOException {
        while (size > 0) {
            final long baseMod = dstOffset % GRANULARITY;
            final long baseOffset = dstOffset - baseMod;
            final CacheEntry entry = getEntry(baseOffset);
            final int writeSize = Math.min(GRANULARITY - (int)baseMod, size);
            System.arraycopy(src, srcOffset, entry.getData(), (int)baseMod, writeSize);
            entry.setModified(true);
            dstOffset += writeSize;
            srcOffset += writeSize;
            size -= writeSize;
        }
    }

    @Override
    public void setSize(long size) throws IOException {
        super.setSize(size);
        invalidate();
    }

    @Override
    public void flush() throws IOException {
        final List<CacheEntry> entries = activeMap.values().stream()
                .filter(CacheEntry::isModified)
                .toList();

        for (CacheEntry entry : entries) {
            writeEntry(entry);
        }
        super.flush();
    }

}
