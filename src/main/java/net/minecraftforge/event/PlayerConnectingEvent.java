/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.event;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * PlayerConnectingEvent is fired when a player is connecting to a server right after the ban and whitelist checks. <br>
 * <br>
 * If the event is canceled, the player will be disconnected with either the set rejection message or a generic disconnected message.<br>
 * <br>
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class PlayerConnectingEvent extends Event {
    private final MinecraftServer server;
    private final NetworkManager networkManager;
    private final GameProfile profile;
    @Nullable
    private final ITextComponent originalMessage;
    @Nullable
    private ITextComponent rejectionMessage;

    public PlayerConnectingEvent(MinecraftServer server, NetworkManager networkManager, GameProfile profile, @Nullable ITextComponent original)
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
    public ITextComponent getOriginalMessage()
    {
        return originalMessage;
    }

    @Nullable
    public ITextComponent getRejectionMessage()
    {
        if (this.isCanceled() && this.rejectionMessage == null) {
            return new TranslationTextComponent("multiplayer.disconnect.generic");
        } else {
            return rejectionMessage;
        }
    }

    public void setRejectionMessage(@Nullable ITextComponent rejectionMessage)
    {
        if (rejectionMessage == null) {
            this.setCanceled(false);
            this.rejectionMessage = null;
        } else {
            this.setCanceled(true);
            this.rejectionMessage = rejectionMessage;
        }
    }

    public MinecraftServer getServer()
    {
        return server;
    }
}
