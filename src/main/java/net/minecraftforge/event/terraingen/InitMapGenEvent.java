package net.minecraftforge.event.terraingen;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.world.gen.MapGenBase;

public class InitMapGenEvent extends Event
{
    /** Use CUSTOM to filter custom event types
     */
    public static enum EventType { CAVE, MINESHAFT, NETHER_BRIDGE, NETHER_CAVE, RAVINE, SCATTERED_FEATURE, STRONGHOLD, VILLAGE, OCEAN_MONUMENT, CUSTOM }

    private final EventType type;
    private final MapGenBase originalGen;
    private MapGenBase newGen;

    InitMapGenEvent(EventType type, MapGenBase original)
    {
        this.type = type;
        this.originalGen = original;
        this.setNewGen(original);
    }
    public EventType getType() { return type; }
    public MapGenBase getOriginalGen() { return originalGen; }
    public MapGenBase getNewGen() { return newGen; }
    public void setNewGen(MapGenBase newGen) { this.newGen = newGen; }
}
