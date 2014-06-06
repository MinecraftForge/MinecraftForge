package net.minecraftforge.oredict;

class CustomHashMap<K, V> {
    private float LOAD_FACTOR = 0.75f;

    private int size;
    private int treshold;
    private Entry[] data;
    private final Hasher hasher;

    public CustomHashMap(Hasher<K> hasher, int capacity)
    {
        capacity = findNextPowerOf2(capacity);
        treshold = (int) (capacity * LOAD_FACTOR);
        data = new Entry[capacity];
        this.hasher = hasher;
    }

    public boolean put(K key, V value)
    {
        if (key == null) return false;
        int hash = hash(key);
        int n = limit(hash);
        for (Entry<K, V> e = data[n]; e != null; e = e.next)
        {
            if (equals(key, e.key)) return false;
        }
        add(n, new Entry(key, value, hash));
        return true;
    }

    public V get(K key)
    {
        if (key == null) return null;
        int hash = hash(key);
        int n = limit(hash);
        for (Entry<K, V> e = data[n]; e != null; e = e.next)
        {
            if (equals(key, e.key)) return e.value;
        }
        return null;
    }

    private void ensureCapacity()
    {
        if (size < treshold) return;
        Entry[] old = data;
        data = new Entry[old.length * 2];
        treshold = (int) (data.length * LOAD_FACTOR);
        for (Entry e : old)
        {
            while (e != null)
            {
                int n = limit(e.hash);
                Entry next = e.next;
                e.next = data[n];
                data[n] = e;
                e = next;
            }
        }
    }

    private void add(int n, Entry entry)
    {
        Entry e = data[n];
        data[n] = entry;
        entry.next = e;
        size++;
        ensureCapacity();
    }

    private int limit(int n)
    {
        return n & (data.length - 1);
    }

    private int hash(K key)
    {
        return hasher.hash(key);
    }

    private boolean equals(K key, K other)
    {
        return hasher.equals(key, other);
    }

    private int findNextPowerOf2(int n)
    {
        n--;
        n |= n >> 1;
        n |= n >> 2;
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;
        return n;
    }

    private static class Entry<K, V> {
        private final int hash;
        private final K key;
        private final V value;
        private Entry next;

        private Entry(K key, V value, int hash)
        {
            this.key = key;
            this.value = value;
            this.hash = hash;
        }
    }

    interface Hasher<T> {
        boolean equals(T first, T second);

        int hash(T object);
    }
}
