package net.minecraftforge.event.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

public class TileEntityEvent extends Event
{
    public final TileEntity tileEntity;
    public TileEntityEvent(TileEntity tileEntity)
    {
        this.tileEntity = tileEntity;
    }

    /**
     * Called whenever a TileEntity's NBTTagCompound is read from. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class LoadNBTEvent extends TileEntityEvent
    {
    	public final NBTTagCompound tileEntityNBT;
        public LoadNBTEvent(TileEntity tileEntity, NBTTagCompound tileEntityNBT)
        {
            super(tileEntity);
            this.tileEntityNBT = tileEntityNBT;
        }
    }

    /**
     * Called whenever a TileEntity's NBTTagCompound is about to be saved. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
     **/
    public static class SaveNBTEvent extends TileEntityEvent
    {
    	public final NBTTagCompound tileEntityNBT;
        public SaveNBTEvent(TileEntity tileEntity, NBTTagCompound tileEntityNBT)
        {
            super(tileEntity);
            this.tileEntityNBT = tileEntityNBT;
        }
    }
}
