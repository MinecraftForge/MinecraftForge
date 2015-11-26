package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLivingEvent<E extends EntityLivingBase> extends Event
{
    public final E entity;
    public final RendererLivingEntity<E> renderer;
    public final double x;
    public final double y;
    public final double z;

    public RenderLivingEvent(E entity, RendererLivingEntity<E> renderer, double x, double y, double z)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    @Cancelable
    public static class Pre<E extends EntityLivingBase> extends RenderLivingEvent<E>
    {
        public Pre(E entity, RendererLivingEntity<E> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }
    public static class Post<E extends EntityLivingBase> extends RenderLivingEvent<E>
    {
        public Post(E entity, RendererLivingEntity<E> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }

    public abstract static class Specials<E extends EntityLivingBase> extends RenderLivingEvent<E>
    {
        public Specials(E entity, RendererLivingEntity<E> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }

        @Cancelable
        public static class Pre<E extends EntityLivingBase> extends Specials<E>
        {
            public Pre(E entity, RendererLivingEntity<E> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
        public static class Post<E extends EntityLivingBase> extends Specials<E>
        {
            public Post(E entity, RendererLivingEntity<E> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
    }
}
