package net.minecraftforge.event.entity;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;

public class EntityItemPickupEvent extends EntityEvent
{
    private final EntityItem item;
    private final EntityPlayer player;
    
    public EntityItemPickupEvent(EntityItem item, EntityPlayer player)
    {
        super(item);
        this.item = item;
        this.player = player;
    }
    
    public EntityItem getItem()
    {
        return item;
    }
    
    public EntityPlayer getPlayer()
    {
        return player;
    }
    
    @Override
    public boolean isCancelable()
    {
        return true;
    }
}
