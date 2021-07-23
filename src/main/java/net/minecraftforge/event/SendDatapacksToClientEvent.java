package net.minecraftforge.event;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fires when a player joins the server or when the reload command is ran,
 * before crafting recipes are sent to the client. Send datapack data to the
 * client when this event fires.
 */
public class SendDatapacksToClientEvent extends Event
{
    private final ServerPlayer player;

    public SendDatapacksToClientEvent(ServerPlayer player)
    {
        this.player = player;
    }

    public ServerPlayer getPlayer()
    {
        return this.player;
    }
}
