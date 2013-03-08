package net.minecraftforge.client.event.entity.player;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RenderPlayerEvent extends PlayerEvent {
    public RenderPlayer playerRenderer;

    public RenderPlayerEvent(EntityPlayer player, RenderPlayer playerRenderer)
    {
        super(player);
        this.playerRenderer = playerRenderer;
    }
    
    @Cancelable
    public static class PreRender extends RenderPlayerEvent {

        public PreRender(EntityPlayer player, RenderPlayer playerRenderer)
        {
            super(player, playerRenderer);
        }

    }
    
    public static class PostRender extends RenderPlayerEvent {

        public PostRender(EntityPlayer player, RenderPlayer playerRenderer)
        {
            super(player, playerRenderer);
        }

    }
    
    @Cancelable
    public static class PreRenderName extends RenderPlayerEvent {

        public PreRenderName(EntityPlayer player, RenderPlayer playerRenderer)
        {
            super(player, playerRenderer);
        }

    }
    
    public static class PostRenderName extends RenderPlayerEvent {

        public PostRenderName(EntityPlayer player, RenderPlayer playerRenderer)
        {
            super(player, playerRenderer);
        }

    }
    
    public static class RenderSpecials extends RenderPlayerEvent {

        public RenderSpecials(EntityPlayer player, RenderPlayer playerRenderer)
        {
            super(player, playerRenderer);
        }

    }
    
    @Cancelable
    public static class SetArmorModel extends RenderPlayerEvent {

        public SetArmorModel(EntityPlayer player, RenderPlayer playerRenderer)
        {
            super(player, playerRenderer);
        }

    }
}
