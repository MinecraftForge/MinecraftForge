package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLivingEvent<T extends EntityLivingBase> extends Event
{
    public final EntityLivingBase entity;
    public final RendererLivingEntity<T> renderer;
    public final double x;
    public final double y;
    public final double z;

    public RenderLivingEvent(EntityLivingBase entity, RendererLivingEntity<T> renderer, double x, double y, double z)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Cancelable
    public static class Pre<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Pre(EntityLivingBase entity, RendererLivingEntity<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }
    public static class Post<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Post(EntityLivingBase entity, RendererLivingEntity<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }

    public abstract static class Specials<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Specials(EntityLivingBase entity, RendererLivingEntity<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }

        @Cancelable
        public static class Pre<T extends EntityLivingBase> extends Specials<T>
        {
            public Pre(EntityLivingBase entity, RendererLivingEntity<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
        public static class Post<T extends EntityLivingBase> extends Specials<T>
        {
            public Post(EntityLivingBase entity, RendererLivingEntity<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
    }
}
