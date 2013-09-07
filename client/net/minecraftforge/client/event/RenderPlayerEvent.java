package net.minecraftforge.client.event;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

public abstract class RenderPlayerEvent extends PlayerEvent
{
    public final RenderPlayer renderer;
    public final float partialRenderTick;

    public RenderPlayerEvent(EntityPlayer player, RenderPlayer renderer, float partialRenderTick)
    {
        super(player);
        this.renderer = renderer;
        this.partialRenderTick = partialRenderTick;
    }

    @Cancelable
    public static class Pre extends RenderPlayerEvent
    {
        public Pre(EntityPlayer player, RenderPlayer renderer, float tick){ super(player, renderer, tick); }
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(EntityPlayer player, RenderPlayer renderer, float tick){ super(player, renderer, tick); }
    }
    
    public abstract static class Specials extends RenderPlayerEvent
    {
        @Deprecated
        public final float partialTicks;
        public Specials(EntityPlayer player, RenderPlayer renderer, float partialTicks)
        {
            super(player, renderer, partialTicks);
            this.partialTicks = partialTicks;
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

    public static class SetArmorModel extends RenderPlayerEvent
    {
        /**
         * Setting this to any value besides -1 will result in the function being 
         * Immediately exited with the return value specified.
         */
        public int result = -1;
        public final int slot;
        @Deprecated
        public final float partialTick;
        public final ItemStack stack;
        public SetArmorModel(EntityPlayer player, RenderPlayer renderer, int slot, float partialTick, ItemStack stack)
        {
            super(player, renderer, partialTick);
            this.slot = slot;
            this.partialTick = partialTick;
            this.stack = stack;
        }
    }
}