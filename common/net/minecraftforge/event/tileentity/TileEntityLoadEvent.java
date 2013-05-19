package net.minecraftforge.event.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class TileEntityLoadEvent extends Event
{
    private TileEntity ret;
    public final NBTTagCompound tag;
    
    public TileEntityLoadEvent(NBTTagCompound tag)
    {
        this.tag = tag;
    }
    
    public TileEntity getLoadedTile()
    {
        return ret;
    }
    
    public void setResult(TileEntity t)
    {
        setCanceled(true);
        ret = t;
    }
}
