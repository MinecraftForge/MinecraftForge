package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerFlyableFallEvent extends PlayerEvent{

    public float distanceFallen;
    public PlayerFlyableFallEvent(EntityPlayer player, float f)
    {
        super(player);
        this.distanceFallen = f;
    }

}
