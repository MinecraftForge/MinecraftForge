package net.minecraftforge.event.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;

/**
 * Event is fired when DimensionManager starts to initiate a new dimension
 * with {@link net.minecraftforge.common.DimensionManager#initDimension(int)},<br>
 * event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 * <br>
 * This event can be used to override instantiated dimensions <br>
 * by using {@link #setAlternativeDimension }
 */
public class DimensionPreloadEvent extends Event {

    private final int dimensionId;
    private final DimensionType type;
    private WorldServer alternativeDimension;

    public DimensionPreloadEvent(int dimensionId, DimensionType type) {
        this.dimensionId = dimensionId;
        this.type = type;
    }

    public int getDimensionId() {
        return this.dimensionId;
    }
    public DimensionType getDimensionType() {
        return this.type;
    }

    public WorldServer getAlternativeDimension() {
        return alternativeDimension;
    }

    public void setAlternativeDimension(WorldServer alternativeDimension) {
        this.alternativeDimension = alternativeDimension;
    }

}
