package net.minecraftforge.event.terraingen;

import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;
import net.minecraftforge.fml.common.eventhandler.Event;

public class InitMapGenEvent extends Event
{
    /** Use CUSTOM to filter custom event types
     */
    public static enum EventType { CAVE, MINESHAFT, NETHER_BRIDGE, NETHER_CAVE, RAVINE, SCATTERED_FEATURE, STRONGHOLD, VILLAGE, OCEAN_MONUMENT, CUSTOM }

    public final EventType type;
    public final MapGenBase originalGen;
    public MapGenBase newGen;
    public final World worldObj;

    InitMapGenEvent(EventType type, MapGenBase original, World worldObj)
    {
        this.type = type;
        this.originalGen = original;
        this.newGen = original;
        this.worldObj = worldObj;
    }
}
