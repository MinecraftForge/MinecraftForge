/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.client.model;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;

import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = "forge", bus = Bus.FORGE, value = Dist.CLIENT)
public class ModelDataManager
{
    private static WeakReference<World> currentWorld = new WeakReference<>(null);
    
    private static final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap<>();
    
    private static final Map<ChunkPos, Map<BlockPos, IModelData>> modelDataCache = new ConcurrentHashMap<>();

    private static void cleanCaches(World world)
    {
        Preconditions.checkNotNull(world, "World must not be null");
        Preconditions.checkArgument(world == Minecraft.getInstance().world, "Cannot use model data for a world other than the current client world");
        if (world != currentWorld.get())
        {
            currentWorld = new WeakReference<>(world);
            needModelDataRefresh.clear();
            modelDataCache.clear();
        }
    }
    
    public static void requestModelDataRefresh(TileEntity te)
    {
        Preconditions.checkNotNull(te, "Tile entity must not be null");
        World world = te.getWorld();

        cleanCaches(world);
        needModelDataRefresh.computeIfAbsent(new ChunkPos(te.getPos()), $ -> Collections.synchronizedSet(new HashSet<>()))
                            .add(te.getPos());
    }
    
    private static void refreshModelData(World world, ChunkPos chunk)
    {        
        cleanCaches(world);
        Set<BlockPos> needUpdate = needModelDataRefresh.remove(chunk);

        if (needUpdate != null)
        {
            Map<BlockPos, IModelData> data = modelDataCache.computeIfAbsent(chunk, $ -> new ConcurrentHashMap<>());
            for (BlockPos pos : needUpdate)
            {
                TileEntity toUpdate = world.getTileEntity(pos);
                if (toUpdate != null && !toUpdate.isRemoved())
                {
                    data.put(pos, toUpdate.getModelData());
                }
                else
                {
                    data.remove(pos);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event)
    {
        if (!event.getChunk().getWorldForge().isRemote()) return;
        
        ChunkPos chunk = event.getChunk().getPos();
        needModelDataRefresh.remove(chunk);
        modelDataCache.remove(chunk);
    }
    
    public static @Nullable IModelData getModelData(World world, BlockPos pos)
    {
        return getModelData(world, new ChunkPos(pos)).get(pos);
    }
    
    public static Map<BlockPos, IModelData> getModelData(World world, ChunkPos pos)
    {
        Preconditions.checkArgument(world.isRemote, "Cannot request model data for server world");
        refreshModelData(world, pos);
        return modelDataCache.getOrDefault(pos, Collections.emptyMap());
    }
}
