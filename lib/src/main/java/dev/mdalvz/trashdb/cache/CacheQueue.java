package dev.mdalvz.trashdb.cache;

class CacheQueue {

    private CacheEntry front;
    private CacheEntry back;

    public boolean isEmpty() {
        return front == null;
    }

    public CacheEntry getFront() {
        return front;
    }

    public void add(CacheEntry entry) {
        if (isEmpty()) {
            front = entry;
        } else {
            back.setForward(entry);
            entry.setBackward(back);
        }
        back = entry;
    }

    public void remove(CacheEntry entry) {
        if (entry == front && entry == back) {
            front = null;
            back = null;
        } else if (entry == front) {
            front = entry.getForward();
            front.setBackward(null);
        } else if (entry == back) {
            back = entry.getBackward();
            back.setForward(null);
        } else {
            entry.getBackward().setForward(entry.getForward());
            entry.getForward().setBackward(entry.getBackward());
        }
        entry.setBackward(null);
        entry.setForward(null);
    }

    public void promote(CacheEntry entry) {
        remove(entry);
        add(entry);
    }

}
