package net.minecraftforge.event.world;

import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;

/**
 * This event is fired when DimensionManager starts to initiate a new dimension
 * with {@link DimensionManager#initDimension(int)},<br>
 * event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 */
public class DimensionPreloadEvent extends Event {

    private final int dimensionId;
    private final DimensionType type;
    private WorldServer alternativeDimensionToLoad;

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

    public WorldServer getAlternativeDimensionToLoad() {
        return alternativeDimensionToLoad;
    }

    public void setAlternativeDimensionToLoad(WorldServer alternativeDimension) {
        this.alternativeDimensionToLoad = alternativeDimension;
    }

}