package net.minecraftforge.event.world;

import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by goreacraft on 17/03/03.
 *
 * This event is fired when DimensionManager is starting to load a new dimmension
 * dimension 0 (Overworld) will not trigger this event
 * {@link DimensionManager#initDimension(int)},
 * <br>
 * This event is {@link Cancelable}.<br>
 * <br>
 * <br>
 * event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 */
@Cancelable
public class DimensionPreloadEvent extends Event {

    private final int dimensionId;
    private final DimensionType type;

    public DimensionPreloadEvent(int dimensionId, DimensionType type) {
        this.dimensionId = dimensionId;
        this.type = type;
    }

    public int getDimensionId() {
        return this.dimensionId;
    }
}