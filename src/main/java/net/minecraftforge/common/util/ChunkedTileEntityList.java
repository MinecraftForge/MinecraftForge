package net.minecraftforge.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChunkedTileEntityList {
	
	private static ChunkPos getChunkPos(TileEntity te)
	{
		return new ChunkPos(te.getPos());
	}
	
	private HashMap<ChunkPos, List<TileEntity>> content;
	
	public ChunkedTileEntityList()
	{
		this.content = new HashMap<>();
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
	
	public boolean containsAll(Collection<TileEntity> tileEntities)
	{
		for (Iterator<TileEntity> iterator = tileEntities.iterator(); iterator.hasNext();) {
			if(!contains(iterator.next()))
				return false;
		}
		return true;
	}
	
	public boolean contains(TileEntity te)
	{
		List<TileEntity> list = getChunkTEList(getChunkPos(te));
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
	
	public void add(Collection<TileEntity> tileEntities)
	{
		for (Iterator<TileEntity> iterator = tileEntities.iterator(); iterator.hasNext();) {
			add(iterator.next());
		}
	}
	
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
	
	public void removeAll(Collection<TileEntity> tileEntities)
	{
		for (Iterator<TileEntity> iterator = tileEntities.iterator(); iterator.hasNext();) {
			remove(iterator.next());
		}
	}
	
	public boolean remove(TileEntity te)
	{
		ChunkPos pos = getChunkPos(te);
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
	
	public void clear()
	{
		content.clear();
	}
	
	@Override
	public String toString()
	{
		return content.toString();
	}

	public boolean isEmpty() {
		return totalCount() > 0;
	}
	
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
	
}
