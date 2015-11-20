package net.minecraftforge.common;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.set.hash.TLongHashSet;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Set;

import net.minecraft.world.ChunkCoordIntPair;

/**
 * Provides a Set class which allows Mods depending on World.activeChunkSet to
 * get a view of our optimized set.
 * 
 * Such Mods may degrade the performance gained by this optimization.
 */
public class CoordFlyweightSet implements Set<ChunkCoordIntPair>
{
    private final TLongHashSet packedSet = new TLongHashSet(32);

    @Override
    public int size()
    {
        return packedSet.size();
    }

    @Override
    public boolean isEmpty()
    {
        return packedSet.isEmpty();
    }

    @Override
    public boolean contains(Object o)
    {
        if (o instanceof ChunkCoordIntPair)
        {
            ChunkCoordIntPair intPair = (ChunkCoordIntPair) o;
            long packed = ForgeChunkManager.packedChunkCoordIntPair(intPair.chunkXPos, intPair.chunkZPos);
            return containsPacked(packed);
        }

        return false;
    }

    public boolean containsPacked(long packedCoord)
    {
        return packedSet.contains(packedCoord);
    }

    @Override
    public java.util.Iterator<ChunkCoordIntPair> iterator()
    {
        return new CoordFlyweightSet.Iterator(packedIterator());
    }

    public TLongIterator packedIterator()
    {
        return packedSet.iterator();
    }

    @Override
    public Object[] toArray()
    {
        return toArray(new Object[size()]);
    }

    @Override
    public <T> T[] toArray(T[] a)
    {
        if (a.length < this.size())
        {
            a = (T[]) Array.newInstance(a.getClass().getComponentType(), this.size());
        }

        int i = 0;
        for (ChunkCoordIntPair fat : this)
        {
            a[i++] = (T) fat;
        }
        return a;
    }

    @Override
    public boolean add(ChunkCoordIntPair e)
    {
        return add(e.chunkXPos, e.chunkZPos);
    }

    public boolean add(int chunkXPos, int chunkZPos)
    {
        return packedSet.add(ForgeChunkManager.packedChunkCoordIntPair(chunkXPos, chunkZPos));
    }

    public boolean addPacked(long intPair)
    {
        return packedSet.add(intPair);
    }

    @Override
    public boolean remove(Object o)
    {
        if (o instanceof ChunkCoordIntPair)
        {
            ChunkCoordIntPair intPair = (ChunkCoordIntPair) o;
            return packedSet.remove(ForgeChunkManager.packedChunkCoordIntPair(intPair.chunkXPos, intPair.chunkZPos));
        }

        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c)
    {
        for (Object o : c)
        {
            if (!contains(o))
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean addAll(Collection<? extends ChunkCoordIntPair> c)
    {
        boolean changed = false;
        for (ChunkCoordIntPair intPair : c)
        {
            changed |= add(intPair);
        }
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c)
    {
        if (c instanceof CoordFlyweightSet)
        {
            return packedSet.retainAll(((CoordFlyweightSet) c).packedSet);
        }

        TLongArrayList retain = null;
        for (Object o : c)
        {
            if (o instanceof ChunkCoordIntPair)
            {
                if (retain == null)
                {
                    retain = new TLongArrayList(c.size());
                }

                ChunkCoordIntPair intPair = (ChunkCoordIntPair) o;
                retain.add(ForgeChunkManager.packedChunkCoordIntPair(intPair.chunkXPos, intPair.chunkZPos));
            }
        }

        if (retain != null)
        {
            return packedSet.retainAll(retain);
        }

        packedSet.clear();
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c)
    {
        boolean changed = false;
        for (Object o : c)
        {
            changed |= remove(o);
        }
        return changed;
    }

    @Override
    public void clear()
    {
        packedSet.clear();
    }

    public static class Iterator implements java.util.Iterator<ChunkCoordIntPair>
    {
        private final TLongIterator impl;

        public Iterator(TLongIterator impl)
        {
            this.impl = impl;
        }

        @Override
        public boolean hasNext()
        {
            return impl.hasNext();
        }

        @Override
        public ChunkCoordIntPair next()
        {
            long packed = nextPacked();
            return new ChunkCoordIntPair(ForgeChunkManager.packedChunkCoordX(packed), ForgeChunkManager.packedChunkCoordZ(packed));
        }

        public long nextPacked()
        {
            return impl.next();
        }
    }
}
