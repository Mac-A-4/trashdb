package dev.mdalvz.trashdb.accessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileAccessorTest extends RandomDataHelper {

    @Mock
    private FileChannel fileChannel;
    private FileAccessor fileAccessor;

    @BeforeEach
    public void beforeEach() throws IOException {
        doReturn((long)Accessor.GRANULARITY).when(fileChannel).size();
        fileAccessor = new FileAccessor(fileChannel);
    }

    @Test
    public void whenReadingData_thenDataReadFromFile() throws IOException {
        //given
        byte[] dst = new byte[0x10];

        //when
        fileAccessor.read(0, 0x10, 0, dst);

        //then
        ArgumentCaptor<ByteBuffer> captor = ArgumentCaptor.forClass(ByteBuffer.class);
        verify(fileChannel).read(captor.capture(), eq((long)0));
        assertEquals(captor.getValue().capacity(), 0x10);
        assertEquals(captor.getValue().limit(), 0x10);
        assertEquals(captor.getValue().position(), 0);
    }

    @Test
    public void whenWritingData_thenDataWrittenToFile() throws IOException {
        //given
        byte[] src = randomData(0x10);

        //when
        fileAccessor.write(0, 0x10, 0, src);

        //then
        ArgumentCaptor<ByteBuffer> captor = ArgumentCaptor.forClass(ByteBuffer.class);
        verify(fileChannel).write(captor.capture(), eq((long)0));
        assertEquals(captor.getValue().capacity(), 0x10);
        assertEquals(captor.getValue().limit(), 0x10);
        assertEquals(captor.getValue().position(), 0);
    }

    @Test
    public void whenGetSize_thenSizeReturned() throws IOException {
        //given
        doReturn((long)0x10).when(fileChannel).size();

        //when
        long size = fileAccessor.getSize();

        //then
        assertEquals(0x10, size);
    }

    @Test
    public void whenSetSize_thenFileTruncated() throws IOException {
        //when
        fileAccessor.setSize(Accessor.GRANULARITY);

        //then
        verify(fileChannel).truncate(Accessor.GRANULARITY);
    }

    @Test
    public void whenSetInvalidSize_thenExceptionIsThrown() throws IOException {
        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            fileAccessor.setSize(Accessor.GRANULARITY / 2);
        });
    }

    @Test
    public void whenFlush_thenFileForced() throws IOException {
        //when
        fileAccessor.flush();

        //then
        verify(fileChannel).force(true);
    }

    @Test
    public void whenConstructingInvalidSize_thenExceptionIsThrown() throws IOException {
        //given
        FileChannel otherChannel = mock(FileChannel.class);
        doReturn((long)Accessor.GRANULARITY / 2).when(otherChannel).size();

        //when & then
        assertThrows(IllegalArgumentException.class, () -> {
            new FileAccessor(otherChannel);
        });
    }

}
