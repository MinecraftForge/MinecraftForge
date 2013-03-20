package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.EntityPlayer;

public class PlayerCreativeFallEvent extends PlayerEvent{

    public float distanceFallen;
    public PlayerCreativeFallEvent(EntityPlayer player, float f)
    {
        super(player);
        this.distanceFallen = f;
    }

}
