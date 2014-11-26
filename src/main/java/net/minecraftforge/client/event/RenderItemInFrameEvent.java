package net.minecraftforge.client.event;

import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * This event is called when an item is rendered in an item frame.
 * 
 * You can set canceled to do no further vanilla processing.
 */
@Cancelable
public class RenderItemInFrameEvent extends Event
{
    public final ItemStack item;
    public final EntityItemFrame entityItemFrame;
    public final RenderItemFrame renderer;
    
    public RenderItemInFrameEvent(EntityItemFrame itemFrame, RenderItemFrame renderItemFrame)
    {
        item = itemFrame.getDisplayedItem();
        entityItemFrame = itemFrame;
        renderer = renderItemFrame;
    }
}
