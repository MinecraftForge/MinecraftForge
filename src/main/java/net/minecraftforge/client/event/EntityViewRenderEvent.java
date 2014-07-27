package net.minecraftforge.client.event;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.Event.Result;

/**
 * Event that hooks into EntityRenderer, allowing any feature to customize visual attributes of
 * fog the player sees.
 */
public abstract class EntityViewRenderEvent extends Event
{
    public final EntityRenderer renderer;
    public final EntityLivingBase entity;
    public final Block block;
    public final double renderPartialTicks;

    public EntityViewRenderEvent(EntityRenderer renderer, EntityLivingBase entity, Block block, double renderPartialTicks)
    {
        this.renderer = renderer;
        this.entity = entity;
        this.block = block;
        this.renderPartialTicks = renderPartialTicks;
    }

    /**
     * Event that allows any feature to customize the fog density the player sees.
     * NOTE: In order to make this event have an effect, you must cancel the event
     */
    @Cancelable
    public static class FogDensity extends EntityViewRenderEvent
    {
        public float density;

        public FogDensity(EntityRenderer renderer, EntityLivingBase entity, Block block, double renderPartialTicks, float density)
        {
            super(renderer, entity, block, renderPartialTicks);
            this.density = density;
        }
    }
    
    /**
     * Event that allows any feature to customize the rendering of fog.
     */
    @HasResult
    public static class RenderFogEvent extends EntityViewRenderEvent
    {
        public final int fogMode;
        public final float farPlaneDistance;
        
        public RenderFogEvent(EntityRenderer renderer, EntityLivingBase entity, Block block, double renderPartialTicks, int fogMode, float farPlaneDistance)
        {
            super(renderer, entity, block, renderPartialTicks);
            this.fogMode = fogMode;
            this.farPlaneDistance = farPlaneDistance;
        }
    }

    /**
     * Event that allows any feature to customize the color of fog the player sees.
     * NOTE: Any change made to one of the color variables will affect the result seen in-game.
     */
    public static class FogColors extends EntityViewRenderEvent
    {
        public float red;
        public float green;
        public float blue;

        public FogColors(EntityRenderer renderer, EntityLivingBase entity, Block block, double renderPartialTicks, float red, float green, float blue)
        {
            super(renderer, entity, block, renderPartialTicks);
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
}
