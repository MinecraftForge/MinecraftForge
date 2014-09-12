package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;

/**
 * This event is used to either take over the rendering a certain layer of armor,
 * by setting the result to -2 (which cancels the rendering of that layer and allows
 * you to render it with the data provided by this event), or change the ShouldRenderPass
 * value, telling the vanilla renderer to add special things, like the enchantment
 * glint, to the current layer being rendered.
 * 
 * This event replaces RenderPlayerEvent.setArmorModel.
 * On each renderpass a different piece of armor is rendered.
 * For each entity wearing armor this event is fired up to four times during one rendertick.
 * 
 * The event is posted when a renderer needs to know if a certain a layer of a armortexture
 * needs special treatment.
 */
public class ShouldRenderPassEvent extends EntityEvent
{

    public final Render render;
    public final float partialTickTime;
    public final int slot;
    public final ItemStack armorStack;
    
    public int result = -1;
    
    public ShouldRenderPassEvent(Entity entity, Render render, float partialTickTime, int slot, ItemStack armorStack)
    {
        super(entity);
        
        this.render = render;
        this.partialTickTime = partialTickTime;
        this.slot = slot;
        this.armorStack = armorStack;
    }
}
