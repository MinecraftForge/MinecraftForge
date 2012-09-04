package net.minecraftforge.event.entity.player;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityItem;
import net.minecraft.src.EntityPlayer;
import net.minecraftforge.event.Cancelable;

@Cancelable
public class EntityItemPickupEvent extends PlayerEvent
{
    public final EntityItem item;
    private boolean handled = false;
    
    public EntityItemPickupEvent(EntityPlayer player, EntityItem item)
    {
        super(player);
        this.item = item;
    }
    
    public boolean isHandled()
    {
        return handled;
    }

    public void setHandled()
    {
        handled = true;
    }
}
