package dev.mdalvz.trashdb.accessor;

import java.util.Random;

public abstract class RandomDataHelper {

    protected byte[] randomData(int size) {
        final byte[] result = new byte[size];
        new Random().nextBytes(result);
        return result;
    }

}
