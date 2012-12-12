package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet20NamedEntitySpawn;

public class PlayerTrackingSentEvent extends PlayerEvent
{
    private final EntityPlayerMP _receiver;

    /**
     * Fired when a player enter into the user's entity
     * tracking zone.
     */
    public PlayerTrackingSentEvent(EntityPlayerMP player, EntityPlayerMP receiver)
    {
        super(player);
        _receiver = receiver;
    }

    public EntityPlayerMP getSpawnedPlayer()
    {
        return (EntityPlayerMP)entityPlayer;
    }

    public EntityPlayerMP getReceiverPlayer()
    {
        return _receiver;
    }
}