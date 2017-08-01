/*
 * Minecraft Forge
 * Copyright (c) 2017.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.minecraftforge.common.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

public class ChunkedTileEntityList implements List<TileEntity>
{

    private static ChunkPos getChunkPos(TileEntity te)
    {
        return new ChunkPos(te.getPos());
    }

    private LinkedHashMap<ChunkPos, List<TileEntity>> content;
    private int totalCount = 0;
    private int modCount = 0;

    public ChunkedTileEntityList()
    {
        this.content = new LinkedHashMap<>();
    }

    public ChunkedTileEntityList(ChunkedTileEntityList list)
    {
        this();
        for (Entry<ChunkPos, List<TileEntity>> entry : list.content.entrySet())
        {
            content.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        updateTotalCount();
    }

    private void updateTotalCount()
    {
        totalCount = 0;
        for (List<TileEntity> values : content.values())
        {
            totalCount += values.size();
        }
    }

    @Override
    public boolean containsAll(Collection<?> tileEntities)
    {
        for (Object object : tileEntities)
        {
            if (!contains(object))
                return false;
        }
        return true;
    }

    @Override
    public boolean contains(Object te)
    {
        if (!(te instanceof TileEntity))
            return false;
        List<TileEntity> list = content.get(getChunkPos((TileEntity) te));
        if (list != null)
            return list.contains(te);
        return false;
    }

    public void add(TileEntity... tileEntities)
    {
        for (int i = 0; i < tileEntities.length; i++)
        {
            add(tileEntities[i]);
        }
    }

    @Override
    public boolean addAll(Collection<? extends TileEntity> tileEntities)
    {
        for (TileEntity tileEntity : tileEntities)
        {
            add(tileEntity);
        }
        return true;
    }

    @Override
    public boolean add(TileEntity te)
    {
        ChunkPos pos = getChunkPos(te);
        List<TileEntity> list = content.get(pos);
        totalCount++;
        modCount++;
        if (list == null)
        {
            list = new ArrayList<>();
            list.add(te);
            content.put(pos, list);
            return true;
        }
        else
            return list.add(te);
    }

    @Override
    @Deprecated
    public void add(int index, TileEntity te)
    {
        ChunkPos pos = getChunkPos(te);
        List<TileEntity> list = content.get(pos);
        totalCount++;
        modCount++;
        if (list == null)
        {
            list = new ArrayList<>();
            list.add(te);
            content.put(pos, list);
        }
        else
        {
            if (index >= list.size())
                list.add(te);
            else
                list.add(index, te);
        }

    }

    @Override
    @Deprecated
    public boolean addAll(int index, Collection<? extends TileEntity> tileEntities)
    {
        for (TileEntity te : tileEntities)
            this.add(index++, te);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> tileEntities)
    {
        boolean removedAll = true;
        for (Object object : tileEntities)
        {
            if (!remove(object))
                removedAll = false;
        }
        return removedAll;
    }

    @Override
    public boolean remove(Object te)
    {
        if (!(te instanceof TileEntity))
            return false;
        ChunkPos pos = getChunkPos((TileEntity) te);
        List<TileEntity> values = content.get(pos);
        if (values != null)
            if (values.remove(te))
            {
                totalCount--;
                modCount++;
                if (values.isEmpty())
                    removeChunk(pos);
                return true;
            }
        return false;
    }

    private boolean removeChunk(ChunkPos key)
    {
        List<TileEntity> removed = content.remove(key);
        if (removed == null)
            return false;

        totalCount -= removed.size();
        modCount++;
        return true;
    }

    public boolean removeChunk(World world, ChunkPos pos, boolean notify)
    {
        if (notify)
        {
            for (TileEntity te : content.get(pos))
            {
                te.onChunkUnload();
            }
        }

        return removeChunk(pos);
    }

    public int chunkCount()
    {
        return content.size();
    }

    @Override
    public void clear()
    {
        totalCount = 0;
        modCount++;
        content.clear();
    }

    @Override
    public String toString()
    {
        return content.toString();
    }

    @Override
    public boolean isEmpty()
    {
        return chunkCount() == 0;
    }

    @Override
    public Iterator<TileEntity> iterator()
    {
        return new Iterator<TileEntity>() {

            int index = 0;
            int expModCount = ChunkedTileEntityList.this.modCount;

            Iterator<List<TileEntity>> iterator = content.values().iterator();

            List<TileEntity> currentList;

            @Override
            public boolean hasNext()
            {
                while (currentList == null || currentList.size() <= index)
                {
                    if (iterator.hasNext())
                    {
                        currentList = iterator.next();
                        index = 0;
                    }
                    else
                        return false;
                }

                return true;
            }

            @Override
            public TileEntity next()
            {
                this.checkCoModifications();
                TileEntity value = currentList.get(index);
                index++;
                return value;
            }

            @Override
            public void remove()
            {
                this.checkCoModifications();
                ChunkedTileEntityList.this.totalCount--;
                currentList.remove(index - 1);
            }

            protected void checkCoModifications()
            {
                if (this.expModCount != ChunkedTileEntityList.this.modCount)
                    throw new ConcurrentModificationException();
            }
        };
    }

    @Override
    public boolean retainAll(Collection<?> paramCollection)
    {
        boolean changed = false;
        totalCount = 0;
        for (Iterator<List<TileEntity>> iterator = content.values().iterator(); iterator.hasNext();)
        {
            List<TileEntity> value = iterator.next();
            if (value.retainAll(paramCollection))
            {
                changed = true;

                modCount++;

                if (value.isEmpty())
                {
                    iterator.remove();
                    continue;
                }
            }

            totalCount += value.size();
        }
        return changed;
    }

    @Override
    public int size()
    {
        return totalCount;
    }

    @Override
    public Object[] toArray()
    {
        Object[] array = new Object[totalCount];
        int i = 0;
        for (List<TileEntity> values : content.values())
        {
            for (int j = 0; j < values.size(); j++)
            {
                array[i] = values.get(j);
                i++;
            }
        }
        return array;
    }

    @Override
    public <T> T[] toArray(T[] paramArrayOfT)
    {
        if (paramArrayOfT.length < totalCount)
            paramArrayOfT = (T[]) Array.newInstance(paramArrayOfT.getClass().getComponentType(), totalCount);
        int i = 0;
        for (List<TileEntity> values : content.values())
        {
            for (TileEntity te : values)
                paramArrayOfT[i++] = (T) te;
        }
        return paramArrayOfT;
    }

    @Override
    @Deprecated
    public TileEntity get(int index)
    {
        this.checkIndex_returnIsCeil(index, false);

        Iterator<List<TileEntity>> itr = ChunkedTileEntityList.this.content.values().iterator();
        List<TileEntity> c;
        int offset = 0;

        while (itr.hasNext())
        {
            c = itr.next();
            if (c.size() + offset > index)
                return c.get(index - offset);
            else
                offset += c.size();
        }
        // should never happen.
        throw new ConcurrentModificationException();
    }

    @Override
    @Deprecated
    public int indexOf(Object o)
    {
        if (o instanceof TileEntity)
        {
            TileEntity te = (TileEntity) o;
            ChunkPos p = ChunkedTileEntityList.getChunkPos(te);
            int offset = 0;

            for (Entry<ChunkPos, List<TileEntity>> e : this.content.entrySet())
            {
                if (e.getKey().equals(p))
                {
                    int i = e.getValue().indexOf(o);
                    if (i < 0)
                        return i;
                    else
                        return offset + i;
                }
                offset += e.getValue().size();
            }
        }
        return -1;
    }

    @Override
    @Deprecated
    public int lastIndexOf(Object o)
    {
        if (o instanceof TileEntity)
        {
            TileEntity te = (TileEntity) o;
            ChunkPos p = ChunkedTileEntityList.getChunkPos(te);
            int offset = 0;

            for (Entry<ChunkPos, List<TileEntity>> e : this.content.entrySet())
            {
                if (e.getKey().equals(p))
                {
                    int i = e.getValue().lastIndexOf(o);
                    if (i < 0)
                        return i;
                    else
                        return offset + i;
                }
                offset += e.getValue().size();
            }
        }
        return -1;
    }

    @Override
    @Deprecated
    public ListIterator<TileEntity> listIterator()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public ListIterator<TileEntity> listIterator(int index)
    {
        throw new UnsupportedOperationException();
    }

    @Override
    @Deprecated
    public TileEntity remove(int index)
    {
        this.checkIndex_returnIsCeil(index, false);

        Iterator<List<TileEntity>> itr = ChunkedTileEntityList.this.content.values().iterator();
        List<TileEntity> c;
        int offset = 0;

        while (itr.hasNext())
        {
            c = itr.next();
            if (c.size() + offset > index)
            {
                this.totalCount--;
                this.modCount++;
                return c.remove(index - offset);
            }
            else
                offset += c.size();
        }
        // should never happen.
        throw new ConcurrentModificationException();
    }
    
    /**
     * This will remove the TileEntity at the given index. and adds the given
     * element at the end of the right chunkList.
     * 
     * @return The TileEntity previous at the given index.
     */
    @Override
    @Deprecated
    public TileEntity set(int index, TileEntity element)
    {
        TileEntity removed = remove(index);
        add(element);
        return removed;
    }

    @Override
    @Deprecated
    public List<TileEntity> subList(int fromIndex, int toIndex)
    {
        throw new UnsupportedOperationException();
    }

    protected boolean checkIndex_returnIsCeil(int index, boolean isAdding)
    {
        int size = this.size();
        if (isAdding && index == size)
            return true;

        if (index < 0 || index > this.size())
            throw new IndexOutOfBoundsException();

        return false;
    }
}
