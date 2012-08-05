package net.minecraftforge.event.entity;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.World;

public class FillBucketEvent extends PlayerEvent
{
    private final ItemStack current;
    private final World world;
    private final MovingObjectPosition target;
    
    private ItemStack result;
    private boolean handeled = false;
    
    public FillBucketEvent(EntityPlayer player, ItemStack current, World world, MovingObjectPosition target)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.target = target;
    }

    @Override
    public boolean isCancelable()
    {
        return true;
    }
    
    public ItemStack getCurrentItem()
    {
        return current;
    }
    
    public World getWorld()
    {
        return world;
    }
    
    public MovingObjectPosition getTarget()
    {
        return target;
    }
    
    public boolean isHandeled()
    {
        return handeled;
    }
    
    public void setHandeled()
    {
        handeled = true;
    }
    
    public ItemStack getResult()
    {
        return result;
    }
    
    public void setResult(ItemStack result)
    {
        this.result = result;
    }

}
