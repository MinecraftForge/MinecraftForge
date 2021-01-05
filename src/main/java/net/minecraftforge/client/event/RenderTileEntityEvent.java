package net.minecraftforge.client.event;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class RenderTileEntityEvent extends Event
{

    private final TileEntity tileentity;

    public RenderTileEntityEvent(TileEntity tileentity)
    {
        this.tileentity = tileentity;
    }

    public TileEntity getTileEntity() { return tileentity; }

}
