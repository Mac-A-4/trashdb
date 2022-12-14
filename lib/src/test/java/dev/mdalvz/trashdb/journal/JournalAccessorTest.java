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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JournalAccessorTest extends RandomDataHelper {

    private JournalAccessor journalAccessor;
    @Mock
    private Accessor baseAccessor;
    @Mock
    private Journal journal;

    @BeforeEach
    public void beforeEach() {
        journalAccessor = new JournalAccessor(baseAccessor, journal);
    }

    @Test
    public void whenWritingData_thenJournalEntriesAdded() throws IOException {
        //given
        byte[] srcData = randomData(Accessor.GRANULARITY * 3);

        //when
        journalAccessor.write(Accessor.GRANULARITY / 2, Accessor.GRANULARITY * 3, 0, srcData);

        //then
        verify(baseAccessor).write(Accessor.GRANULARITY / 2, Accessor.GRANULARITY * 3, 0, srcData);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(journal, times(4)).addEntry(captor.capture(), any());
        List<Long> expectedOffsets = List.of(
                (long)0,
                (long)Accessor.GRANULARITY,
                (long)Accessor.GRANULARITY * 2,
                (long)Accessor.GRANULARITY * 3);
        assertArrayEquals(captor.getAllValues().toArray(new Long[0]), expectedOffsets.toArray());
    }

    @Test
    public void whenWritingData_thenExistingEntriesNotAdded() throws IOException {
        //given
        byte[] srcData = randomData(Accessor.GRANULARITY * 3);

        //when
        journalAccessor.write(Accessor.GRANULARITY / 2, Accessor.GRANULARITY * 3, 0, srcData);
        journalAccessor.write(Accessor.GRANULARITY / 2, Accessor.GRANULARITY * 4, 0, srcData);

        //then
        verify(baseAccessor, times(2)).write(eq((long)(Accessor.GRANULARITY / 2)), anyInt(), eq(0), eq(srcData));
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(journal, times(5)).addEntry(captor.capture(), any());
        List<Long> expectedOffsets = List.of(
                (long)0,
                (long)Accessor.GRANULARITY,
                (long)Accessor.GRANULARITY * 2,
                (long)Accessor.GRANULARITY * 3,
                (long)Accessor.GRANULARITY * 4);
        assertArrayEquals(captor.getAllValues().toArray(new Long[0]), expectedOffsets.toArray());
    }

    @Test
    public void whenSetSize_thenJournalEntriesAdded() throws IOException {
        //given
        doReturn((long)(Accessor.GRANULARITY * 8)).when(baseAccessor).getSize();

        //when
        journalAccessor.setSize(Accessor.GRANULARITY * 4);

        //then
        verify(baseAccessor).setSize(Accessor.GRANULARITY * 4);
        ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);
        verify(journal, times(4)).addEntry(captor.capture(), any());
        List<Long> expectedOffsets = List.of(
                (long)Accessor.GRANULARITY * 4,
                (long)Accessor.GRANULARITY * 5,
                (long)Accessor.GRANULARITY * 6,
                (long)Accessor.GRANULARITY * 7);
        assertArrayEquals(captor.getAllValues().toArray(new Long[0]), expectedOffsets.toArray());
    }

    @Test
    public void whenWritingData_thenAccessorIsRestored() throws IOException {
        //given
        int G = Accessor.GRANULARITY;
        Accessor original = new MemoryAccessor(G * 4);
        original.write(0, G * 4, 0, randomData(G * 4));
        Accessor modified = copyAccessor(original);
        Accessor journalData = new MemoryAccessor();
        Journal journal = new Journal(new Serializer(journalData));
        journal.activate(modified.getSize());
        Accessor journalAccessor = new JournalAccessor(modified, journal);

        //when & then
        assertTrue(compareAccessor(modified, original));
        journalAccessor.write(0, G, 0, randomData(G));
        journalAccessor.write(G * 2, G, 0, randomData(G));
        journalAccessor.setSize(G * 3);
        assertFalse(compareAccessor(modified, original));
        journal.restore(modified);
        assertTrue(compareAccessor(modified, original));
    }

    private Accessor copyAccessor(Accessor a) throws IOException {
        long size = a.getSize();
        byte[] x = new byte[(int)size];
        a.read(0, (int)size, 0, x);
        Accessor b = new MemoryAccessor((int)size);
        b.write(0, (int)size, 0, x);
        return b;
    }

    private boolean compareAccessor(Accessor a, Accessor b) throws IOException {
        long size = a.getSize();
        if (size != b.getSize()) {
            return false;
        }
        byte[] x = new byte[(int)size];
        byte[] y = new byte[(int)size];
        a.read(0, (int)size, 0, x);
        b.read(0, (int)size, 0, y);
        return Arrays.compare(x, y) == 0;
    }

}
