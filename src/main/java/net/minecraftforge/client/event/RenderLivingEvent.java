package net.minecraftforge.client.event;

import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

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
    
    public static class SetArmorModel extends RenderLivingEvent
    {
        /**
         * Setting this to any value besides -1 will result in the function being 
         * Immediately exited with the return value specified.
         */
        public int result = -1;
        public final int slot;
        public final ItemStack stack;
        public final float partialTickTime;
        
        public SetArmorModel(EntityLivingBase entity, RendererLivingEntity renderer, int slot, float partialTick, ItemStack stack)
        {
            super(entity, renderer, entity.posX, entity.posY, entity.posZ);
            this.slot = slot;
            this.stack = stack;
            this.partialTickTime = partialTick;
        }
    }
}
