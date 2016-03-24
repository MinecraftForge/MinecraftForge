package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;

public abstract class RenderLivingEvent<T extends EntityLivingBase> extends Event
{
    private final EntityLivingBase entity;
    private final RenderLivingBase<T> renderer;
    private final double x;
    private final double y;
    private final double z;

    public RenderLivingEvent(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z)
    {
        this.entity = entity;
        this.renderer = renderer;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public EntityLivingBase getEntity() { return entity; }
    public RenderLivingBase<T> getRenderer() { return renderer; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }

    @Cancelable
    public static class Pre<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Pre(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }
    public static class Post<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Post(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
    }

    public abstract static class Specials<T extends EntityLivingBase> extends RenderLivingEvent<T>
    {
        public Specials(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }

        @Cancelable
        public static class Pre<T extends EntityLivingBase> extends Specials<T>
        {
            public Pre(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
        public static class Post<T extends EntityLivingBase> extends Specials<T>
        {
            public Post(EntityLivingBase entity, RenderLivingBase<T> renderer, double x, double y, double z){ super(entity, renderer, x, y, z); }
        }
    }
}
