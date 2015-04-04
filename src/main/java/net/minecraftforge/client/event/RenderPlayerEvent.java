package net.minecraftforge.client.event;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    public final RenderPlayer renderer;
    public final float partialRenderTick;
    public final double x;
    public final double y;
    public final double z;

    public RenderPlayerEvent(EntityPlayer player, RenderPlayer renderer, float partialRenderTick, double x, double y, double z)
    {
        super(player);
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(EntityPlayer player, RenderPlayer renderer, float tick, double x, double y, double z){ super(player, renderer, tick, x, y, z); }
        @Deprecated
        public Pre(EntityPlayer player, RenderPlayer renderer, float tick){ this(player, renderer, tick, 0D, 0D, 0D); }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(EntityPlayer player, RenderPlayer renderer, float tick, double x, double y, double z){ super(player, renderer, tick, x, y, z); }
        @Deprecated
        public Post(EntityPlayer player, RenderPlayer renderer, float tick){ this(player, renderer, tick, 0D, 0D, 0D); }
    }
    
    @Deprecated
    public abstract static class Specials extends RenderPlayerEvent
    {
        public Specials(EntityPlayer player, RenderPlayer renderer, float partialTicks)
        {
            super(player, renderer, partialTicks, 0D, 0D, 0D);
        }

        @Cancelable
        public static class Pre extends Specials
        {
            public boolean renderHelmet = true;
            public boolean renderCape = true;
            public boolean renderItem = true;
            public Pre(EntityPlayer player, RenderPlayer renderer, float partialTicks){ super(player, renderer, partialTicks); }
        }

        public static class Post extends Specials
        {
            public Post(EntityPlayer player, RenderPlayer renderer, float partialTicks){ super(player, renderer, partialTicks); }
        }
    }

    @Deprecated
    public static class SetArmorModel extends RenderPlayerEvent
    {
        /**
         * Setting this to any value besides -1 will result in the function being 
         * Immediately exited with the return value specified.
         */
        public int result = -1;
        public final int slot;
        public final ItemStack stack;
        public SetArmorModel(EntityPlayer player, RenderPlayer renderer, int slot, float partialTick, ItemStack stack)
        {
            super(player, renderer, partialTick, 0D, 0D, 0D);
            this.slot = slot;
            this.stack = stack;
        }
    }
}