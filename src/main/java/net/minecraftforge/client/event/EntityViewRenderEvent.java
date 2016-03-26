package net.minecraftforge.client.event;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Event that hooks into EntityRenderer, allowing any feature to customize visual attributes
 *  the player sees.
 */
public abstract class EntityViewRenderEvent extends Event
{
    public final EntityRenderer renderer;
    public final Entity entity;
    public final Block block;
    public final double renderPartialTicks;

    public EntityViewRenderEvent(EntityRenderer renderer, Entity entity, Block block, double renderPartialTicks)
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

        public FogDensity(EntityRenderer renderer, Entity entity, Block block, double renderPartialTicks, float density)
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

        public RenderFogEvent(EntityRenderer renderer, Entity entity, Block block, double renderPartialTicks, int fogMode, float farPlaneDistance)
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

        public FogColors(EntityRenderer renderer, Entity entity, Block block, double renderPartialTicks, float red, float green, float blue)
        {
            super(renderer, entity, block, renderPartialTicks);
            this.red = red;
            this.green = green;
            this.blue = blue;
        }
    }
    
    /** 
     * Event that allows mods to alter the angles of the player's camera. Mainly useful for applying roll.
     */
    public static class CameraSetup extends EntityViewRenderEvent
    {
        public float yaw;
        public float pitch;
        public float roll;

        public CameraSetup(EntityRenderer renderer, Entity entity, Block block, double renderPartialTicks, float yaw, float pitch, float roll)
        {
            super(renderer, entity, block, renderPartialTicks);
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }
    }
    
    /**
     * Event that allows mods to alter the raw FOV itself.
     * This directly affects to the FOV without being modified.
     * */
    public static class FOVModifier extends EntityViewRenderEvent
    {
        private float fov;
        
        public FOVModifier(EntityRenderer renderer, Entity entity, Block block, double renderPartialTicks, float fov) {
            super(renderer, entity, block, renderPartialTicks);
            this.setFOV(fov);
        }

        public float getFOV() {
            return fov;
        }

        public void setFOV(float fov) {
            this.fov = fov;
        }
    }
}
