package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.Event;

public abstract class RenderLivingEvent extends Event
{
    public final EntityLiving entity;
    public final RenderLiving renderer;

    public RenderLivingEvent(EntityLiving entity, RenderLiving renderer)
    {
        this.entity = entity;
        this.renderer = renderer;
    }

    public abstract static class Specials extends RenderLivingEvent
    {
        public Specials(EntityLiving entity, RenderLiving renderer){ super(entity, renderer); }

        @Cancelable
        public static class Pre extends Specials
        {
            public Pre(EntityLiving entity, RenderLiving renderer){ super(entity, renderer); }
        }
        public static class Post extends Specials
        {
            public Post(EntityLiving entity, RenderLiving renderer){ super(entity, renderer); }
        }
    }
}
