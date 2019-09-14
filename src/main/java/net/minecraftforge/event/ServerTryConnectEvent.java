package net.minecraftforge.event;

import javax.annotation.Nullable;

import org.apache.commons.lang3.Validate;

import com.mojang.authlib.GameProfile;

import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * ServerTryConnectEvent is fired as part of checking if a user is permitted to connect to the server. <br>
 * This event is fired via {@link ForgeEventFactory#canUserConnect(NetworkManager, GameProfile, String)},
 * which is executed by the {@link NetHandlerLoginServer#tryAcceptPlayer()}<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/

public class ServerTryConnectEvent extends Event {

    private final MinecraftServer server;
    private final NetworkManager networkManager;
    private final GameProfile profile;
    @Nullable
    private final String originalMessage;
    @Nullable
    private String rejectionMessage;

    public ServerTryConnectEvent(MinecraftServer server, NetworkManager networkManager, GameProfile profile, @Nullable String original)
    {
        this.server = server;
        this.networkManager = networkManager;
        this.profile = profile;
        this.originalMessage = original;
        this.setRejectionMessage(original);
    }

    public NetworkManager getNetworkManager()
    {
        return networkManager;
    }

    public GameProfile getProfile()
    {
        return profile;
    }

    @Nullable
    public String getOriginalMessage()
    {
        return originalMessage;
    }

    @Nullable
    public String getRejectionMessage()
    {
        return rejectionMessage;
    }

    public boolean isRejected()
    {
        return this.rejectionMessage != null;
    }

    public boolean isAccepted()
    {
        return this.rejectionMessage == null;
    }

    public void reject(String message)
    {
        Validate.notNull(message, "Rejection message cannot be null");
        this.rejectionMessage = message;
    }

    public void accept()
    {
        this.rejectionMessage = null;
    }

    public void setRejectionMessage(@Nullable String rejectionMessage)
    {
        this.rejectionMessage = rejectionMessage;
    }

    public MinecraftServer getServer()
    {
        return server;
    }
}
