package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.client.renderer.RenderGlobal;

@Cancelable
public class DrawBlockHighlightEvent extends Event
{
    public final RenderGlobal context;
    public final EntityPlayer player;
    public final RayTraceResult target;
    public final int subID;
    public final ItemStack currentItem;
    public final float partialTicks;

    public DrawBlockHighlightEvent(RenderGlobal context, EntityPlayer player, RayTraceResult target, int subID, ItemStack currentItem, float partialTicks)
    {
        this.context = context;
        this.player = player;
        this.target = target;
        this.subID = subID;
        this.currentItem = currentItem;
        this.partialTicks= partialTicks;
    }

}
