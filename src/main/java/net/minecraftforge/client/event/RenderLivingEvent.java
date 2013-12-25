package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLivingEvent extends Event
{
    public final EntityLivingBase entity;
    public final RendererLivingEntity renderer;
    public final double x;
    public final double y;
    public final double z;

    public RenderLivingEvent(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Cancelable
    public static class Pre extends RenderLivingEvent
    {
        public Pre(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }
    public static class Post extends RenderLivingEvent
    {
        public Post(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }

    public abstract static class Specials extends RenderLivingEvent
    {
        public Specials(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }

        @Cancelable
        public static class Pre extends Specials
        {
            public Pre(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
        public static class Post extends Specials
        {
            public Post(EntityLivingBase entity, RendererLivingEntity renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
    }
}
