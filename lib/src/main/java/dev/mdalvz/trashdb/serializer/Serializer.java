package dev.mdalvz.trashdb.serializer;

import dev.mdalvz.trashdb.accessor.Accessor;
import dev.mdalvz.trashdb.accessor.DecoratorAccessor;

import java.io.IOException;

public class Serializer extends DecoratorAccessor {

    public static final int LONG_SIZE = 8;
    public static final int BYTE_SIZE = 1;
    public static final int BOOLEAN_SIZE = BYTE_SIZE;

    public Serializer(Accessor base) {
        super(base);
    }

    public void readStruct(long srcOffset, Struct struct) throws IOException {
        struct.read(this, srcOffset);
    }

    public void writeStruct(long dstOffset, Struct struct) throws IOException {
        struct.write(this, dstOffset);
    }

    private final byte[] longBuffer = new byte[LONG_SIZE];

    public long readLong(long srcOffset) throws IOException {
        read(srcOffset, longBuffer.length, 0, longBuffer);
        return ((long)longBuffer[0] << 56) +
                ((long)(longBuffer[1] & 0xFF) << 48) +
                ((long)(longBuffer[2] & 0xFF) << 40) +
                ((long)(longBuffer[3] & 0xFF) << 32) +
                ((long)(longBuffer[4] & 0xFF) << 24) +
                ((longBuffer[5] & 0xFF) << 16) +
                ((longBuffer[6] & 0xFF) << 8) +
                ((longBuffer[7] & 0xFF));
    }

    public void writeLong(long dstOffset, long value) throws IOException {
        longBuffer[0] = (byte)(value >>> 56);
        longBuffer[1] = (byte)(value >>> 48);
        longBuffer[2] = (byte)(value >>> 40);
        longBuffer[3] = (byte)(value >>> 32);
        longBuffer[4] = (byte)(value >>> 24);
        longBuffer[5] = (byte)(value >>> 16);
        longBuffer[6] = (byte)(value >>> 8);
        longBuffer[7] = (byte)(value);
        write(dstOffset, longBuffer.length, 0, longBuffer);
    }

    private final byte[] byteBuffer = new byte[BYTE_SIZE];

    public byte readByte(long srcOffset) throws IOException {
        read(srcOffset, byteBuffer.length, 0, byteBuffer);
        return byteBuffer[0];
    }

    public void writeByte(long dstOffset, byte value) throws IOException {
        byteBuffer[0] = value;
        write(dstOffset, byteBuffer.length, 0, byteBuffer);
    }

    public boolean readBoolean(long srcOffset) throws IOException {
        return readByte(srcOffset) != 0;
    }

    public void writeBoolean(long dstOffset, boolean value) throws IOException {
        writeByte(dstOffset, (byte)(value ? 1 : 0));
    }

}
