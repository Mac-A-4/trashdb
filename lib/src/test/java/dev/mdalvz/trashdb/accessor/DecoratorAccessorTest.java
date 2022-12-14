package dev.mdalvz.trashdb.accessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DecoratorAccessorTest extends RandomDataHelper {

    @Mock
    private Accessor baseAccessor;
    private DecoratorAccessor decoratorAccessor;

    @BeforeEach
    public void beforeEach() {
        decoratorAccessor = new DecoratorAccessor(baseAccessor);
    }

    @Test
    public void whenReadingData_thenForwardedToBase() throws IOException {
        //given
        byte[] data = randomData(0x10);

        //when
        decoratorAccessor.read(0, 0x10, 0, data);

        //then
        verify(baseAccessor).read(0, 0x10, 0, data);
    }

    @Test
    public void whenWritingData_thenForwardedToBase() throws IOException {
        //given
        byte[] data = randomData(0x10);

        //when
        decoratorAccessor.write(0, 0x10, 0, data);

        //then
        verify(baseAccessor).write(0, 0x10, 0, data);
    }

    @Test
    public void whenGetSize_thenForwardedToBase() throws IOException {
        //given
        doReturn((long)0x10).when(baseAccessor).getSize();

        //when
        long size = decoratorAccessor.getSize();

        //then
        assertEquals(size, 0x10);
    }

    @Test
    public void whenFlush_thenForwardedToBase() throws IOException {
        //when
        decoratorAccessor.flush();

        //then
        verify(baseAccessor).flush();
    }

    @Test
    public void whenSetSize_thenForwardedToBase() throws IOException {
        //when
        decoratorAccessor.setSize(0x10);

        //then
        verify(baseAccessor).setSize(0x10);
    }

}
