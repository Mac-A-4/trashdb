package dev.mdalvz.trashdb.cache;

import dev.mdalvz.trashdb.accessor.Accessor;

class CacheEntry {

    private CacheEntry backward;
    private CacheEntry forward;
    private long offset;
    private boolean modified;
    private final byte[] data = new byte[Accessor.GRANULARITY];

    public CacheEntry getBackward() {
        return backward;
    }

    public void setBackward(CacheEntry backward) {
        this.backward = backward;
    }

    public CacheEntry getForward() {
        return forward;
    }

    public void setForward(CacheEntry forward) {
        this.forward = forward;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public byte[] getData() {
        return data;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

}
