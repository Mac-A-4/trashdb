package dev.mdalvz.trashdb.serializer;

import java.io.IOException;

public interface Struct {

    void read(Serializer serializer, long srcOffset) throws IOException;

    void write(Serializer serializer, long dstOffset) throws IOException;

}
