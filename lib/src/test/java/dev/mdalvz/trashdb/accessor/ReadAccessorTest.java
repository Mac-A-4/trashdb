package dev.mdalvz.trashdb.accessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ReadAccessorTest extends RandomDataHelper {

    @Mock
    Accessor baseAccessor;
    ReadAccessor readAccessor;

    @BeforeEach
    public void beforeEach() {
        readAccessor = new ReadAccessor(baseAccessor);
    }

    @Test
    public void whenReadingData_thenDataReadFromBase() throws IOException {
        //given
        byte[] src = randomData(0x10);
        byte[] dst = new byte[0x10];
        doAnswer(invocation -> {
            System.arraycopy(src, 0, dst, 0, 0x10);
            return null;
        }).when(baseAccessor).read(0, 0x10, 0, dst);

        //when
        readAccessor.read(0, 0x10, 0, dst);

        //then
        assertArrayEquals(src, dst);
    }

    @Test
    public void whenGetSize_thenSizeReturned() throws IOException {
        //given
        doReturn((long)Accessor.GRANULARITY).when(baseAccessor).getSize();

        //when
        long size = readAccessor.getSize();

        //then
        assertEquals(size, Accessor.GRANULARITY);
    }

    @Test
    public void whenWritingData_thenExceptionIsThrown() {
        //given
        byte[] src = randomData(0x10);

        //when & then
        assertThrows(IllegalStateException.class, () -> {
            readAccessor.write(0, src.length, 0, src);
        });
    }

    @Test
    public void whenSetSize_thenExceptionIsThrown() {
        //when & then
        assertThrows(IllegalStateException.class, () -> {
            readAccessor.setSize(Accessor.GRANULARITY);
        });
    }

    @Test
    public void whenFlush_thenExceptionIsThrown() {
        //when & then
        assertThrows(IllegalStateException.class, () -> {
            readAccessor.flush();
        });
    }

}
