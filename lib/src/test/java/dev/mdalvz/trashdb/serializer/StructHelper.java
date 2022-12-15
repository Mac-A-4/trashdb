package dev.mdalvz.trashdb.serializer;

import java.io.IOException;
import java.util.Objects;

public class StructHelper implements Struct {

    private static final int LONG_OFFSET = 0;
    private static final int BYTE_OFFSET = 8;
    private static final int BOOLEAN_OFFSET = 9;
    private long longValue;
    private byte byteValue;
    private boolean booleanValue;

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public byte getByteValue() {
        return byteValue;
    }

    public void setByteValue(byte byteValue) {
        this.byteValue = byteValue;
    }

    public boolean getBooleanValue() {
        return booleanValue;
    }

    public void setBooleanValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    @Override
    public void read(Serializer serializer, long srcOffset) throws IOException {
        setLongValue(serializer.readLong(srcOffset + LONG_OFFSET));
        setByteValue(serializer.readByte(srcOffset + BYTE_OFFSET));
        setBooleanValue(serializer.readBoolean(srcOffset + BOOLEAN_OFFSET));
    }

    @Override
    public void write(Serializer serializer, long dstOffset) throws IOException {
        serializer.writeLong(dstOffset + LONG_OFFSET, getLongValue());
        serializer.writeByte(dstOffset + BYTE_OFFSET, getByteValue());
        serializer.writeBoolean(dstOffset + BOOLEAN_OFFSET, getBooleanValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StructHelper that = (StructHelper) o;
        return getLongValue() == that.getLongValue() &&
                getByteValue() == that.getByteValue() &&
                getBooleanValue() == that.getBooleanValue();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLongValue(), getByteValue(), getBooleanValue());
    }

}
