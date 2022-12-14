package dev.mdalvz.trashdb.journal;

import dev.mdalvz.trashdb.accessor.Accessor;
import dev.mdalvz.trashdb.struct.Serializer;

import java.io.IOException;

public class Journal {

    private final JournalHeader header;
    private final JournalEntry entry;
    private final Serializer serializer;

    public Journal(Serializer serializer) throws IOException {
        this.serializer = serializer;
        header = new JournalHeader();
        entry = new JournalEntry();
        if (serializer.getSize() == 0) {
            resize(JournalHeader.SIZE);
            header.setActive(false);
            header.setOriginalSize(0);
            header.setEntryCount(0);
            writeHeader();
            serializer.flush();
        }
    }

    public boolean isActive() throws IOException {
        readHeader();
        return header.isActive();
    }

    public void activate(long originalSize) throws IOException {
        readHeader();
        if (header.isActive()) {
            throw new IllegalStateException("Cannot activate an active Journal");
        }
        header.setActive(true);
        header.setOriginalSize(originalSize);
        header.setEntryCount(0);
        writeHeader();
        serializer.flush();
    }

    public void deactivate() throws IOException {
        readHeader();
        if (!header.isActive()) {
            throw new IllegalStateException("Cannot deactivate an inactive Journal");
        }
        header.setActive(false);
        header.setOriginalSize(0);
        header.setEntryCount(0);
        writeHeader();
        serializer.flush();
    }

    public void addEntry(long offset, byte[] data) throws IOException {
        readHeader();
        if (!header.isActive()) {
            throw new IllegalStateException("Cannot get an entry from an inactive Journal");
        }
        entry.setOffset(offset);
        System.arraycopy(data, 0, entry.getData(), 0, JournalEntry.DATA_SIZE);
        final long entryCount = header.getEntryCount();
        writeEntry(entryCount);
        header.setEntryCount(entryCount + 1);
        writeHeader();
        serializer.flush();
    }

    public void restore(Accessor accessor) throws IOException {
        readHeader();
        if (!header.isActive()) {
            throw new IllegalStateException("Cannot restore using an inactive Journal");
        }
        accessor.setSize(header.getOriginalSize());
        for (long i = 0; i < header.getEntryCount(); ++i) {
            readEntry(i);
            accessor.write(entry.getOffset(), JournalEntry.DATA_SIZE, 0, entry.getData());
        }
        accessor.flush();
    }

    private void resize(long size) throws IOException {
        long currentSize = serializer.getSize();
        if (size < currentSize) {
            return;
        }
        while (currentSize < size) {
            if (currentSize == 0) {
                currentSize = Accessor.GRANULARITY;
            } else {
                currentSize *= 2;
            }
        }
        serializer.setSize(currentSize);
    }

    private void readHeader() throws IOException {
        serializer.readStruct(0, header);
    }

    private void writeHeader() throws IOException {
        serializer.writeStruct(0, header);
    }

    private void readEntry(long index) throws IOException {
        serializer.readStruct(getEntryOffset(index), entry);
    }

    private void writeEntry(long index) throws IOException {
        final long offset = getEntryOffset(index);
        resize(offset + JournalEntry.SIZE);
        serializer.writeStruct(offset, entry);
    }

    private long getEntryOffset(long index) {
        return (long)JournalHeader.SIZE + (long)JournalEntry.SIZE * index;
    }

}
