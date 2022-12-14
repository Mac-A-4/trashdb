package dev.mdalvz.trashdb.struct;

import dev.mdalvz.trashdb.accessor.Accessor;
import dev.mdalvz.trashdb.accessor.RandomDataHelper;
import dev.mdalvz.trashdb.accessor.MemoryAccessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SerializerTest extends RandomDataHelper {

    private final int SIZE = Accessor.GRANULARITY;
    private MemoryAccessor memoryAccessor;
    private Serializer serializer;

    @BeforeEach
    public void beforeEach() {
        memoryAccessor = new MemoryAccessor();
        memoryAccessor.setSize(SIZE);
        memoryAccessor.write(0, SIZE, 0, randomData(SIZE));
        serializer = new Serializer(memoryAccessor);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, 1, 5, 7, -1, -5, -7, Long.MAX_VALUE, Long.MIN_VALUE})
    public void whenReadAndWriteLong_thenDataIsCorrect(long value) throws IOException {
        //when
        serializer.writeLong(0, value);

        //then
        assertEquals(serializer.readLong(0), value);
    }

    @ParameterizedTest
    @ValueSource(bytes = {0, 1, 5, 7, -1, -5, -7, Byte.MAX_VALUE, Byte.MIN_VALUE})
    public void whenReadAndWriteByte_thenDataIsCorrect(byte value) throws IOException {
        //when
        serializer.writeByte(0, value);

        //then
        assertEquals(serializer.readByte(0), value);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    public void whenReadAndWriteBoolean_thenDataIsCorrect(boolean value) throws IOException {
        //when
        serializer.writeBoolean(0, value);

        //then
        assertEquals(serializer.readBoolean(0), value);
    }

    @Test
    public void whenReadAndWriteStruct_thenDataIsCorrect() throws IOException {
        //given
        StructHelper struct = new StructHelper();
        struct.setLongValue(0x101010);
        struct.setByteValue((byte)0x20);
        struct.setBooleanValue(true);

        //when
        serializer.writeStruct(0, struct);
        StructHelper other = new StructHelper();
        serializer.readStruct(0, other);

        //then
        assertEquals(struct, other);
    }

}
