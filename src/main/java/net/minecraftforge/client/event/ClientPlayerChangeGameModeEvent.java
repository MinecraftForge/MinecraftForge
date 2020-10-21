package net.minecraftforge.client.event;

import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.world.GameType;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired before the client player is notified of a change in game mode from the server.
 */
public class ClientPlayerChangeGameModeEvent extends Event
{
    private final NetworkPlayerInfo info;
    private final GameType currentGameMode;
    private final GameType newGameMode;

    public ClientPlayerChangeGameModeEvent(NetworkPlayerInfo info, GameType currentGameMode, GameType newGameMode)
    {
        this.info = info;
        this.currentGameMode = currentGameMode;
        this.newGameMode = newGameMode;
    }

    public NetworkPlayerInfo getInfo()
    {
        return info;
    }

    public GameType getCurrentGameMode()
    {
        return currentGameMode;
    }

    public GameType getNewGameMode()
    {
        return newGameMode;
    }
}
