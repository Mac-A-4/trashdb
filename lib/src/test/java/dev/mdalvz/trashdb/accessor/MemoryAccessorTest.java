package dev.mdalvz.trashdb.accessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MemoryAccessorTest extends RandomDataHelper {

    private static final int SIZE = Accessor.GRANULARITY;
    private byte[] accessorData;
    private MemoryAccessor memoryAccessor;

    @BeforeEach
    public void beforeEach() {
        accessorData = randomData(SIZE);
        memoryAccessor = new MemoryAccessor(SIZE);
        memoryAccessor.write(0, SIZE, 0, accessorData);
    }

    @Test
    public void whenReadingData_thenDataIsReadCorrectly() {
        //given
        final byte[] dstData = new byte[1];

        //when
        memoryAccessor.read(0, 1, 0, dstData);

        //then
        assertEquals(dstData[0], accessorData[0]);
    }

    @Test
    public void whenWritingData_thenDataIsWrittenCorrectly() {
        //given
        final byte[] srcData = randomData(1);
        final byte[] dstData = new byte[1];

        //when
        memoryAccessor.write(0, 1, 0, srcData);
        memoryAccessor.read(0, 1, 0, dstData);

        //then
        assertEquals(dstData[0], srcData[0]);
    }

    @Test
    public void whenGettingSize_thenSizeIsReturnedCorrectly() {
        //when
        final int size = (int)memoryAccessor.getSize();

        //then
        assertEquals(size, SIZE);
    }

    @Test
    public void whenSettingSize_thenSizeIsReturnedCorrectly() {
        //given
        final int newSize = Accessor.GRANULARITY * 2;

        //when
        memoryAccessor.setSize(newSize);
        final int size = (int)memoryAccessor.getSize();

        //then
        assertEquals(size, newSize);
    }

    @Test
    public void whenSettingInvalidSize_thenExceptionIsThrown() {
        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            memoryAccessor.setSize(Accessor.GRANULARITY / 2);
        });
    }

    @Test
    public void whenConstructingInvalidSize_thenExceptionIsThrown() {
        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            new MemoryAccessor(Accessor.GRANULARITY / 2);
        });
    }

}
