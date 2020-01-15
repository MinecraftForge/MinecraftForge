package net.minecraftforge.event;

import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * SChatPacketEvent is fired before a SChatPacket is sent from the server to a client. <br>
 * This event is fired via {@link ForgeHooks#onSChatPacket(SChatPacket, ServerPlayerEntity)} )},
 * which is executed by the {@link net.minecraft.network.play.ServerPlayNetHandler#sendPacket(IPacket, GenericFutureListener)} <br>
 * <br>
 * {@link #originalPacket} Contains the original packet that would have been sent.<br>
 * {@link #modifiedPacket} Contains the modified packet that will be sent if the event is not cancelled.<br>
 * {@link #player} Contains the server player entity for the player which the packet will be sent to.<br>
 * <br>
 * This event is {@link Cancelable}. <br>
 * If this event is canceled, the chat packet is never sent to the client.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class SChatPacketEvent extends Event {
    private final SChatPacket originalPacket;
    @Nullable
    private SChatPacket modifiedPacket;
    private ServerPlayerEntity player;
    public SChatPacketEvent(SChatPacket packet, ServerPlayerEntity player)
    {
        this.originalPacket = this.modifiedPacket = packet;
        this.player = player;
    }

    public SChatPacket getOriginalPacket()
    {
        return originalPacket;
    }

    @Nullable
    public SChatPacket getModifiedPacket()
    {
        return modifiedPacket;
    }

    public void setModifiedPacket(@Nullable SChatPacket modifiedPacket)
    {
        this.modifiedPacket = modifiedPacket;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }
}
