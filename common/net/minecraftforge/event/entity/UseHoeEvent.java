package net.minecraftforge.event.entity;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class UseHoeEvent extends PlayerEvent
{

    public final ItemStack current;
    public final World world;
    public final int x;
    public final int y;
    public final int z;
    
    private boolean handeled = false;
    
    public UseHoeEvent(EntityPlayer player, ItemStack current, World world, int x, int y, int z)
    {
        super(player);
        this.current = current;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Override
    public boolean isCancelable()
    {
        return true;
    }

    public boolean isHandeled()
    {
        return handeled;
    }
    
    public void setHandeled()
    {
        handeled = true;
    }
}
