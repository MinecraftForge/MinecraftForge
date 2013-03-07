package net.minecraftforge.client.event.entity.player;

import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.Cancelable;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RenderPlayerPostEvent extends PlayerEvent {
    public RenderPlayer playerRenderer;
    
    public RenderPlayerPostEvent(EntityPlayer player, RenderPlayer playerRenderer)
    {
        super(player);
        this.playerRenderer = playerRenderer;
    }

}
