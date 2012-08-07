package net.minecraftforge.client.event;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ItemStack;
import net.minecraft.src.MovingObjectPosition;
import net.minecraft.src.RenderGlobal;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

@Cancelable
public class DrawBlockHighlightEvent extends Event
{
    public final RenderGlobal context;
    public final EntityPlayer player;
    public final MovingObjectPosition target;
    public final int subID;
    public final ItemStack currentItem;
    public final float partialTicks;
    
    public DrawBlockHighlightEvent(RenderGlobal context, EntityPlayer player, MovingObjectPosition target, int subID, ItemStack currentItem, float partialTicks)
    {
        this.context = context;
        this.player = player;
        this.target = target;
        this.subID = subID;
        this.currentItem = currentItem;
        this.partialTicks= partialTicks;
    }

}
