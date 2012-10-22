package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class FillBucketEvent extends PlayerEvent
{
    public final ItemStack current;
    public final World world;
    public final MovingObjectPosition target;
    
    public ItemStack result;
    private boolean handled = false;
    
    public FillBucketEvent(EntityPlayer player, ItemStack current, World world, MovingObjectPosition target)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.target = target;
    }
    
    public boolean isHandled()
    {
        return handled;
    }
    
    public void setHandled()
    {
        handled = true;
    }
    
    @Deprecated
    public void setHandeled()
    {
        this.setHandled();
    }
}
