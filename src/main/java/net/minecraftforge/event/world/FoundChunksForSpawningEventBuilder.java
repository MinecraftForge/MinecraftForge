package net.minecraftforge.event.world;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.WorldServer;

public class FoundChunksForSpawningEventBuilder
{
    public final WorldServer worldServer;
    public int i = -1;
    public final ImmutableList.Builder<ChunkPos> eligibleChunksForSpawning = new ImmutableList.Builder<ChunkPos>();
    public final ImmutableMap.Builder<EnumCreatureType, FoundChunksForSpawningEventCreatureTypeData> creatureTypeData = new ImmutableMap.Builder<EnumCreatureType, FoundChunksForSpawningEventCreatureTypeData>();
    public Exception newEntityException;

    public FoundChunksForSpawningEventBuilder(WorldServer worldServerIn)
    {
        worldServer = worldServerIn;
    }
}
