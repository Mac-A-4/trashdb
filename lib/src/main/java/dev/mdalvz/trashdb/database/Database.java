package dev.mdalvz.trashdb.database;

import dev.mdalvz.trashdb.serializer.Serializer;

import java.io.IOException;

public class Database {

    private final Serializer serializer;
    private final DatabaseHeader header;

    public Database(Serializer serializer) {
        this.serializer = serializer;
        header = new DatabaseHeader();
    }

    private void readHeader() throws IOException {
        serializer.readStruct(0, header);
    }

    private void writeHeader() throws IOException {
        serializer.writeStruct(0, header);
    }

    private long getReferenceAddress(long reference) {
        if (reference > header.getTableSize()) {
            throw new IndexOutOfBoundsException();
        }
        return header.getTableOffset() + (reference - 1) * Serializer.LONG_SIZE;
    }

    private long readReference(long reference) throws IOException {
        return serializer.readLong(getReferenceAddress(reference));
    }

    private void writeReference(long reference, long value) throws IOException {
        serializer.writeLong(getReferenceAddress(reference), value);
    }

    private DatabaseObjectHeader readObjectHeader(long reference) throws IOException {
        DatabaseObjectHeader objectHeader = new DatabaseObjectHeader();
        final long offset = readReference(reference);
        if (offset == 0) {
            throw new IllegalArgumentException("Cannot read object header of a null reference");
        }
        serializer.readStruct(offset, objectHeader);
        return objectHeader;
    }

    private void writeObjectHeader(long reference, DatabaseObjectHeader objectHeader) throws IOException {
        final long offset = readReference(reference);
        if (offset == 0) {
            throw new IllegalArgumentException("Cannot write object header of a null reference");
        }
        serializer.writeStruct(offset, objectHeader);
    }

}
