package dev.mdalvz.trashdb.cache;

import dev.mdalvz.trashdb.accessor.Accessor;
import dev.mdalvz.trashdb.accessor.CountingAccessor;
import dev.mdalvz.trashdb.accessor.MemoryAccessor;
import dev.mdalvz.trashdb.accessor.RandomDataHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class CacheAccessorTest extends RandomDataHelper {

    private final int SIZE = Accessor.GRANULARITY * 16;
    private static final int CAPACITY = 4;
    private byte[] accessorData;
    private MemoryAccessor memoryAccessor;
    private CountingAccessor countingAccessor;
    private CacheAccessor cacheAccessor;

    private void assertSpanEquals(long offset, int size) throws IOException {
        byte[] cacheData = new byte[size];
        cacheAccessor.read(offset, size, 0, cacheData);
        byte[] originalData = new byte[size];
        memoryAccessor.read(offset, size, 0, originalData);
        assertArrayEquals(originalData, cacheData);
    }

    private void assertSpanEquals(long offset, byte[] data) throws IOException {
        byte[] cacheData = new byte[data.length];
        cacheAccessor.read(offset, data.length, 0, cacheData);
        assertArrayEquals(cacheData, data);
    }

    private void assertReadCountEquals(int readCount) {
        assertEquals(countingAccessor.getReadCount(), readCount);
    }

    private void assertWriteCountEquals(int writeCount) {
        assertEquals(countingAccessor.getWriteCount(), writeCount);
    }

    @BeforeEach
    public void beforeEach() {
        accessorData = randomData(SIZE);
        memoryAccessor = new MemoryAccessor();
        memoryAccessor.setSize(SIZE);
        memoryAccessor.write(0, SIZE, 0, accessorData);
        countingAccessor = new CountingAccessor(memoryAccessor);
        cacheAccessor = new CacheAccessor(countingAccessor, CAPACITY);
    }

    @Test
    public void whenReadingInactiveData_thenDataIsReadFromBase() throws IOException {
        //when & then
        assertSpanEquals(0, CacheAccessor.GRANULARITY);
        assertReadCountEquals(1);
        assertWriteCountEquals(0);
    }

    @Test
    public void whenReadingActiveData_thenDataIsReadFromCache() throws IOException {
        //when & then
        assertSpanEquals(0, CacheAccessor.GRANULARITY);
        assertSpanEquals(0, CacheAccessor.GRANULARITY);
        assertReadCountEquals(1);
        assertWriteCountEquals(0);
    }

    @Test
    public void whenReadingInactiveAcrossGranularity_thenDataIsReadCorrectly() throws IOException {
        //when & then
        assertSpanEquals(0, CacheAccessor.GRANULARITY * 2);
        assertReadCountEquals(2);
        assertWriteCountEquals(0);
    }

    @Test
    public void whenReadingActiveAcrossGranularity_thenDataIsReadCorrectly() throws IOException {
        //when & then
        assertSpanEquals(0, CacheAccessor.GRANULARITY * 2);
        assertSpanEquals(0, CacheAccessor.GRANULARITY * 2);
        assertReadCountEquals(2);
        assertWriteCountEquals(0);
    }

    @Test
    public void whenReadingMixedAcrossGranularity_thenDataIsReadCorrectly() throws IOException {
        //when & then
        assertSpanEquals(0, CacheAccessor.GRANULARITY * 2);
        assertSpanEquals(0, CacheAccessor.GRANULARITY * 3);
        assertReadCountEquals(3);
        assertWriteCountEquals(0);
    }

    @Test
    public void whenCacheCapacityExceeded_thenLeastRecentlyUsedEvicted() throws IOException {
        //when & then
        for (int i = 0; i < CAPACITY; ++i) {
            assertSpanEquals(CacheAccessor.GRANULARITY * i, CacheAccessor.GRANULARITY);
        }
        assertReadCountEquals(CAPACITY);
        assertSpanEquals(CacheAccessor.GRANULARITY * CAPACITY, CacheAccessor.GRANULARITY);
        assertReadCountEquals(CAPACITY + 1);
        assertSpanEquals(0, CacheAccessor.GRANULARITY);
        assertReadCountEquals(CAPACITY + 2);
        assertWriteCountEquals(0);
    }

    @Test
    public void whenReadingInvalidOffset_thenExceptionIsThrown() {
        //when & then
        assertThrows(IndexOutOfBoundsException.class, () -> {
            byte[] dst = new byte[1];
            cacheAccessor.read(CacheAccessor.GRANULARITY * 17, 1, 0, dst);
        });
    }

    @Test
    public void whenWriting_thenDataIsWrittenCorrectly() throws IOException {
        //when & then
        byte[] src = randomData(0x10);
        cacheAccessor.write(0, 0x10, 0, src);
        assertSpanEquals(0, src);
        assertReadCountEquals(1);
        assertWriteCountEquals(0);
        cacheAccessor.flush();
        assertReadCountEquals(1);
        assertWriteCountEquals(1);
    }

    @Test
    public void whenCacheCapacityExceeded_thenDataIsWrittenCorrectly() throws IOException {
        //when & then
        byte[] src = randomData(0x10);
        byte[] dst = new byte[0x10];
        for (int i = 0; i < CAPACITY; ++i) {
            cacheAccessor.write(CacheAccessor.GRANULARITY * i, 0x10, 0, src);
        }
        assertReadCountEquals(4);
        assertWriteCountEquals(0);
        cacheAccessor.read(CacheAccessor.GRANULARITY * CAPACITY, 0x10, 0, dst);
        assertReadCountEquals(5);
        assertWriteCountEquals(1);
    }

    @Test
    public void whenSetSize_thenInvalidEntriesEvicted() throws IOException {
        //when & then
        byte[] src = randomData(0x10);
        byte[] dst = new byte[0x10];
        cacheAccessor.write(CacheAccessor.GRANULARITY * 2, 0x10, 0, src);
        assertReadCountEquals(1);
        assertWriteCountEquals(0);
        cacheAccessor.read(CacheAccessor.GRANULARITY * 2, 0x10, 0, dst);
        assertArrayEquals(dst, src);
        assertReadCountEquals(1);
        assertWriteCountEquals(0);
        cacheAccessor.setSize(CacheAccessor.GRANULARITY);
        assertReadCountEquals(1);
        assertWriteCountEquals(0);
        assertThrows(IndexOutOfBoundsException.class, () -> {
            cacheAccessor.read(CacheAccessor.GRANULARITY * 2, 0x10, 0, dst);
        });
    }

}
