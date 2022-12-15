package dev.mdalvz.trashdb.journal;

import dev.mdalvz.trashdb.accessor.Accessor;
import dev.mdalvz.trashdb.accessor.MemoryAccessor;
import dev.mdalvz.trashdb.accessor.RandomDataHelper;
import dev.mdalvz.trashdb.serializer.Serializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JournalTest extends RandomDataHelper {

    @Mock
    private Serializer serializer;
    private Journal journal;

    @BeforeEach
    public void beforeEach() throws IOException {
        doReturn((long)Accessor.GRANULARITY).when(serializer).getSize();
        journal = new Journal(serializer);
    }

    @Test
    public void whenConstructingInvalidSize_thenSerializerIsResized() throws IOException {
        //given
        Serializer serializer = mock(Serializer.class);
        doReturn((long)0).when(serializer).getSize();

        //when
        Journal journal = new Journal(serializer);

        //then
        verify(serializer).setSize(Accessor.GRANULARITY);
    }

    @Test
    public void whenConstructingInvalidSize_thenHeaderIsWritten() throws IOException {
        //given
        Serializer serializer = mock(Serializer.class);
        doReturn((long)0).when(serializer).getSize();

        //when
        Journal journal = new Journal(serializer);

        //then
        ArgumentCaptor<JournalHeader> captor = ArgumentCaptor.forClass(JournalHeader.class);
        verify(serializer).writeStruct(eq((long)0), captor.capture());
        assertFalse(captor.getValue().isActive());
        assertEquals(captor.getValue().getOriginalSize(), 0);
        assertEquals(captor.getValue().getEntryCount(), 0);
        verify(serializer).flush();
    }

    @Test
    public void whenConstructingValidSize_thenSerializerUnchanged() throws IOException {
        //given
        Serializer serializer = mock(Serializer.class);
        doReturn((long)Accessor.GRANULARITY).when(serializer).getSize();

        //when
        Journal journal = new Journal(serializer);

        //then
        verify(serializer, never()).setSize(anyLong());
        verify(serializer, never()).writeStruct(anyLong(), any());
    }

    @Test
    public void whenActivate_thenHeaderWritten() throws IOException {
        //given
        long originalSize = Accessor.GRANULARITY * 10;
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(false);
            header.setOriginalSize(0);
            header.setEntryCount(0);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));

        //when
        journal.activate(originalSize);

        //then
        ArgumentCaptor<JournalHeader> captor = ArgumentCaptor.forClass(JournalHeader.class);
        verify(serializer).writeStruct(eq((long)0), captor.capture());
        assertTrue(captor.getValue().isActive());
        assertEquals(captor.getValue().getOriginalSize(), originalSize);
        assertEquals(captor.getValue().getEntryCount(), 0);
        verify(serializer).flush();
    }

    @Test
    public void whenActivateAlreadyActive_thenExceptionIsThrown() throws IOException {
        //given
        long originalSize = Accessor.GRANULARITY * 10;
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(true);
            header.setOriginalSize(originalSize);
            header.setEntryCount(0);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));

        //when & then
        assertThrows(IllegalStateException.class, () -> {
            journal.activate(originalSize);
        });
    }

    @Test
    public void whenReadIsActive_thenCorrectValueReturned() throws IOException {
        //given
        long originalSize = Accessor.GRANULARITY * 10;
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(true);
            header.setOriginalSize(originalSize);
            header.setEntryCount(0);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));

        //when
        boolean active = journal.isActive();

        //then
        assertTrue(active);
    }

    @Test
    public void whenDeactivate_thenHeaderWritten() throws IOException {
        //given
        long originalSize = Accessor.GRANULARITY * 10;
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(true);
            header.setOriginalSize(originalSize);
            header.setEntryCount(0);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));

        //when
        journal.deactivate();

        //then
        ArgumentCaptor<JournalHeader> captor = ArgumentCaptor.forClass(JournalHeader.class);
        verify(serializer).writeStruct(eq((long)0), captor.capture());
        assertFalse(captor.getValue().isActive());
        assertEquals(captor.getValue().getOriginalSize(), 0);
        assertEquals(captor.getValue().getEntryCount(), 0);
        verify(serializer).flush();
    }

    @Test
    public void whenDeactivateAlreadyInactive_thenExceptionIsThrown() throws IOException {
        //given
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(false);
            header.setOriginalSize(0);
            header.setEntryCount(0);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));

        //when & then
        assertThrows(IllegalStateException.class, () -> {
            journal.deactivate();
        });
    }

    @Test
    public void whenAddingEntry_thenSerializerIsResized() throws IOException {
        //given
        long originalSize = Accessor.GRANULARITY * 10;
        long offset = Accessor.GRANULARITY * 5;
        byte[] data = randomData(JournalEntry.DATA_SIZE);
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(true);
            header.setOriginalSize(originalSize);
            header.setEntryCount(0);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));
        doReturn((long)Accessor.GRANULARITY).when(serializer).getSize();

        //when
        journal.addEntry(offset, data);

        //then
        verify(serializer).setSize(Accessor.GRANULARITY * 2);
    }

    @Test
    public void whenAddingEntry_thenEntryAndHeaderAreWritten() throws IOException {
        //given
        long originalSize = Accessor.GRANULARITY * 10;
        long offset = Accessor.GRANULARITY * 5;
        byte[] data = randomData(JournalEntry.DATA_SIZE);
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(true);
            header.setOriginalSize(originalSize);
            header.setEntryCount(0);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));
        doReturn((long)Accessor.GRANULARITY * 2).when(serializer).getSize();

        //when
        journal.addEntry(offset, data);

        //then
        verify(serializer, never()).setSize(anyLong());
        ArgumentCaptor<JournalHeader> headerCaptor = ArgumentCaptor.forClass(JournalHeader.class);
        verify(serializer).writeStruct(eq((long)0), headerCaptor.capture());
        assertEquals(headerCaptor.getValue().getEntryCount(), 1);
        ArgumentCaptor<JournalEntry> entryCaptor = ArgumentCaptor.forClass(JournalEntry.class);
        verify(serializer).writeStruct(eq((long)(JournalHeader.SIZE)), entryCaptor.capture());
        assertEquals(entryCaptor.getValue().getOffset(), offset);
        assertArrayEquals(entryCaptor.getValue().getData(), data);
        verify(serializer).flush();
    }

    @Test
    public void whenRestoringInactiveJournal_thenExceptionIsThrown() throws IOException {
        //given
        Accessor accessor = mock(Accessor.class);
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(false);
            header.setOriginalSize(0);
            header.setEntryCount(0);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));

        //when & then
        assertThrows(IllegalStateException.class, () -> {
            journal.restore(accessor);
        });
    }

    @Test
    public void whenRestoring_thenAccessorIsRestored() throws IOException {
        //given
        Accessor accessor = new MemoryAccessor(JournalEntry.DATA_SIZE * 3);
        accessor.write(0, (int)accessor.getSize(), 0, randomData((int)accessor.getSize()));
        byte[] srcData = randomData(JournalEntry.DATA_SIZE * 2);
        doAnswer(invocation -> {
            JournalHeader header = invocation.getArgument(1);
            header.setActive(true);
            header.setOriginalSize(Accessor.GRANULARITY * 2);
            header.setEntryCount(2);
            return null;
        }).when(serializer).readStruct(eq((long)0), any(JournalHeader.class));
        doAnswer(invocation -> {
            JournalEntry entry = invocation.getArgument(1);
            entry.setOffset(0);
            System.arraycopy(srcData, 0, entry.getData(), 0, JournalEntry.DATA_SIZE);
            return null;
        }).when(serializer).readStruct(eq((long)(JournalHeader.SIZE)), any(JournalEntry.class));
        doAnswer(invocation -> {
            JournalEntry entry = invocation.getArgument(1);
            entry.setOffset(Accessor.GRANULARITY);
            System.arraycopy(srcData, JournalEntry.DATA_SIZE, entry.getData(), 0, JournalEntry.DATA_SIZE);
            return null;
        }).when(serializer).readStruct(eq((long)(JournalHeader.SIZE + JournalEntry.SIZE)), any(JournalEntry.class));

        //when
        journal.restore(accessor);

        //then
        assertEquals(accessor.getSize(), Accessor.GRANULARITY * 2);
        byte[] dstData = new byte[Accessor.GRANULARITY * 2];
        accessor.read(0, Accessor.GRANULARITY * 2, 0, dstData);
        assertArrayEquals(srcData, dstData);
    }

}
