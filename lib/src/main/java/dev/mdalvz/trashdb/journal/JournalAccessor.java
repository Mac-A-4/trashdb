package dev.mdalvz.trashdb.journal;

import dev.mdalvz.trashdb.accessor.Accessor;
import dev.mdalvz.trashdb.accessor.DecoratorAccessor;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class JournalAccessor extends DecoratorAccessor {

    private final Journal journal;
    private final Set<Long> journalHistory = new HashSet<>();
    private final byte[] data = new byte[JournalEntry.DATA_SIZE];

    public JournalAccessor(Accessor base, Journal journal) {
        super(base);
        this.journal = journal;
    }

    @Override
    public void write(long dstOffset, int size, int srcOffset, byte[] src) throws IOException {
        addRange(dstOffset, size);
        super.write(dstOffset, size, srcOffset, src);
    }

    @Override
    public void setSize(long size) throws IOException {
        final long currentSize = getSize();
        if (size < currentSize) {
            addRange(size, (int)(currentSize - size));
        }
        super.setSize(size);
    }

    private void addRange(long offset, int size) throws IOException {
        final long right = offset + size;
        offset -= (offset % JournalEntry.DATA_SIZE);
        while (offset < right) {
            addEntry(offset);
            offset += JournalEntry.DATA_SIZE;
        }
    }

    private void addEntry(long offset) throws IOException {
        if (journalHistory.contains(offset)) {
            return;
        }
        super.read(offset, JournalEntry.DATA_SIZE, 0, data);
        journal.addEntry(offset, data);
        journalHistory.add(offset);
    }

}
