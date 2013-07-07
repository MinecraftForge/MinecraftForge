package org.bukkit.craftbukkit.util;

public class FlatMap<V> {

    private static final int FLAT_LOOKUP_SIZE = 512;
    private final Object[][] flatLookup = new Object[FLAT_LOOKUP_SIZE * 2][FLAT_LOOKUP_SIZE * 2];

    public void put(long msw, long lsw, V value) {
        long acx = Math.abs(msw);
        long acz = Math.abs(lsw);
        if (acx < FLAT_LOOKUP_SIZE && acz < FLAT_LOOKUP_SIZE) {
            flatLookup[(int) (msw + FLAT_LOOKUP_SIZE)][(int) (lsw + FLAT_LOOKUP_SIZE)] = value;
        }
    }

    public void put(long key, V value) {
        put(LongHash.msw(key), LongHash.lsw(key), value);

    }

    public V get(long msw, long lsw) {
        long acx = Math.abs(msw);
        long acz = Math.abs(lsw);
        if (acx < FLAT_LOOKUP_SIZE && acz < FLAT_LOOKUP_SIZE) {
            return (V) flatLookup[(int) (msw + FLAT_LOOKUP_SIZE)][(int) (lsw + FLAT_LOOKUP_SIZE)];
        } else {
            return null;
        }
    }

    public V get(long key) {
        return get(LongHash.msw(key), LongHash.lsw(key));
    }
}
