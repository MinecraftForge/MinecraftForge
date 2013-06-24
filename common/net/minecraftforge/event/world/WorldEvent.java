package net.minecraftforge.event.world;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.SpawnListEntry;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public class WorldEvent extends Event
{
    public final World world;

    public WorldEvent(World world)
    {
        this.world = world;
    }

    public static class Load extends WorldEvent
    {
        public Load(World world) { super(world); }
    }

    public static class Unload extends WorldEvent
    {
        public Unload(World world) { super(world); }
    }

    public static class Save extends WorldEvent
    {
        public Save(World world) { super(world); }
    }

    /**
     * Called by WorldServer to gather a list of all possible entities that can spawn at the specified location.
     * Canceling the event will result in a empty list, meaning no entity will be spawned.
     */
    @Cancelable
    public static class PotentialSpawns extends WorldEvent
    {
        public final EnumCreatureType type;
        public final int x;
        public final int y;
        public final int z;
        public final List<SpawnListEntry> list;

        public PotentialSpawns(World world, EnumCreatureType type, int x, int y, int z, List oldList)
        {
            super(world);
            this.x = x;
            this.y = y;
            this.z = z;
            this.type = type;
            if (oldList != null)
            {
                this.list = (List<SpawnListEntry>)oldList;
            }
            else
            {
                this.list = new ArrayList<SpawnListEntry>();
            }
        }
    }
}
