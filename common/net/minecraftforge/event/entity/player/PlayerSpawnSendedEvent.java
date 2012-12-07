package net.minecraftforge.event.entity.player;

import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.Packet20NamedEntitySpawn;

public class PlayerSpawnSendedEvent extends PlayerEvent
{
    private final EntityPlayerMP _receiver;

    public PlayerSpawnSendedEvent(EntityPlayerMP player, EntityPlayerMP receiver)
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
