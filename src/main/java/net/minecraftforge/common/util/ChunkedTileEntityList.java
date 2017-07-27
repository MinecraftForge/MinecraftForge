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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;

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
	
	public ChunkedTileEntityList()
	{
		this.content = new LinkedHashMap<>();
	}
	
	public ChunkedTileEntityList(ChunkedTileEntityList list)
	{
		this();
		for (Iterator<Entry<ChunkPos, List<TileEntity>>> iterator = list.entrySet().iterator(); iterator.hasNext();) {
			Entry<ChunkPos, List<TileEntity>> entry = iterator.next();
			content.put(entry.getKey(), new ArrayList<>(entry.getValue()));
		}
	}
	
	public List<TileEntity> getChunkTEList(ChunkPos key)
	{
		return content.get(key);
	}
	
	public Set<ChunkPos> keySet()
	{
		return content.keySet();
	}
	
	public Collection<List<TileEntity>> values()
	{
		return content.values();
	}
	
	public Set<Entry<ChunkPos, List<TileEntity>>> entrySet()
	{
		return content.entrySet();
	}
	
	@Override
	public boolean containsAll(Collection<?> tileEntities)
	{
		for (Iterator<?> iterator = tileEntities.iterator(); iterator.hasNext();) {
			if(!contains(iterator.next()))
				return false;
		}
		return true;
	}
	
	@Override
	public boolean contains(Object te)
	{
		if(!(te instanceof TileEntity))
			return false;
		List<TileEntity> list = getChunkTEList(getChunkPos((TileEntity) te));
		if(list != null)
			return list.contains(te);
		return false;
	}
	
	public void add(TileEntity[] tileEntities)
	{
		for (int i = 0; i < tileEntities.length; i++) {
			add(tileEntities[i]);
		}
	}
	
	@Override
	public boolean addAll(Collection<? extends TileEntity> tileEntities)
	{
		for (Iterator<? extends TileEntity> iterator = tileEntities.iterator(); iterator.hasNext();) {
			add(iterator.next());
		}
		return true;
	}
	
	@Override
	public boolean add(TileEntity te)
	{
		ChunkPos pos = getChunkPos(te);
		List<TileEntity> list = getChunkTEList(pos);
		if(list == null)
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
	public void add(int index, TileEntity te) {
		add(te);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends TileEntity> tileEntities) {
		return addAll(tileEntities);
	}
	
	@Override
	public boolean removeAll(Collection<?> tileEntities)
	{
		boolean removedAll = true;
		for (Iterator<?> iterator = tileEntities.iterator(); iterator.hasNext();) {
			if(!remove(iterator.next()))
				removedAll = false;
		}
		return removedAll;
	}
	
	@Override
	public boolean remove(Object te)
	{
		if(!(te instanceof TileEntity))
			return false;
		ChunkPos pos = getChunkPos((TileEntity) te);
		List<TileEntity> values = getChunkTEList(pos);
		if(values != null)
			if(values.remove(te))
			{
				if(values.isEmpty())
					removeChunk(pos);
				return true;
			}
		return false;
	}
	
	private boolean removeChunk(ChunkPos key)
	{
		return content.remove(key) != null;
	}
	
	public boolean removeChunk(World world, ChunkPos pos, boolean notify)
	{
		if(notify)
		{
			for (TileEntity te : getChunkTEList(pos))
	        {
				te.onChunkUnload();
	        }
		}
		
		return removeChunk(pos);
	}
	
	public int totalCount()
	{
		int size = 0;
		for (List<TileEntity> values : content.values()) {
			size += values.size();
		}
		return size;
	}
	
	public int chunkCount()
	{
		return content.size();
	}
	
	@Override
	public void clear()
	{
		content.clear();
	}
	
	@Override
	public String toString()
	{
		return content.toString();
	}
	
	@Override
	public boolean isEmpty() {
		return totalCount() > 0;
	}
	
	@Override
	public Iterator<TileEntity> iterator()
	{
		return new Iterator<TileEntity>() {
			
			int index = 0;
			
			Iterator<List<TileEntity>> iterator = values().iterator();
			
			List<TileEntity> currentList;
			
			@Override
			public boolean hasNext() {
				while(currentList == null || currentList.size() <= index)
				{
					if(iterator.hasNext())
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
			public TileEntity next() {
				TileEntity value = currentList.get(index);
				index++;
				return value;
			}
			
			@Override
			public void remove() {
				currentList.remove(index-1);
			}
		};
	}

	@Override
	public boolean retainAll(Collection<?> paramCollection) {
		boolean changed = false;
		for (List<TileEntity> values : content.values()) {
			if(values.retainAll(paramCollection))
				changed = true;
		}
		return changed;
	}

	@Override
	public int size() {
		return totalCount();
	}

	@Override
	public Object[] toArray() {
		Object[] array = new Object[totalCount()];
		int i = 0;
		for (List<TileEntity> values : content.values()) {
			for (int j = 0; j < values.size(); j++) {
				array[i] = values.get(j);
				i++;
			}
		}
		return array;
	}

	@Override
	public <T> T[] toArray(T[] paramArrayOfT) {
		if(paramArrayOfT.length < totalCount())
			paramArrayOfT = (T[]) java.lang.reflect.Array.newInstance(paramArrayOfT.getClass().getComponentType(), totalCount());
		int i = 0;
		for (List<TileEntity> values : content.values()) {
			for (int j = 0; j < values.size(); j++) {
				paramArrayOfT[i] = (T) values.get(j);
				i++;
			}
		}
		return paramArrayOfT;
	}
	
	//Not supported methods

	@Override
	public TileEntity get(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int indexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int lastIndexOf(Object o) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<TileEntity> listIterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public ListIterator<TileEntity> listIterator(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TileEntity remove(int index) {
		throw new UnsupportedOperationException();
	}

	@Override
	public TileEntity set(int index, TileEntity element) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<TileEntity> subList(int fromIndex, int toIndex) {
		throw new UnsupportedOperationException();
	}
	
}
