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
    }

    public static class Post extends RenderPlayerEvent
    {
        public Post(EntityPlayer player, RenderPlayer renderer, float tick, double x, double y, double z){ super(player, renderer, tick, x, y, z); }
    }
}