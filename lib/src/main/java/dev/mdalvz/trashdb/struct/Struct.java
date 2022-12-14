package dev.mdalvz.trashdb.struct;

import java.io.IOException;

public interface Struct {

    void read(Serializer serializer, long srcOffset) throws IOException;

    void write(Serializer serializer, long dstOffset) throws IOException;

}
