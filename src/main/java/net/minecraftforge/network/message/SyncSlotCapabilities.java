package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncSlotCapabilities {

    private final int entityId;
    private final int slotIndex;
    private final PacketBuffer capabilityData;

    public SyncSlotCapabilities(int entityId, int slotIndex, PacketBuffer capabilityData)
    {
        this.entityId = entityId;
        this.slotIndex = slotIndex;
        this.capabilityData = capabilityData;
    }

    public void encode(PacketBuffer out)
    {
        out.writeVarInt(this.entityId);
        out.writeShort(this.slotIndex);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
    }

    public static SyncSlotCapabilities decode(PacketBuffer in)
    {
        int entityId = in.readVarInt();
        int slotIndex = in.readShort();
        byte[] capabilityData = new byte[in.readVarInt()];
        in.readBytes(capabilityData);
        return new SyncSlotCapabilities(entityId, slotIndex,
            new PacketBuffer(Unpooled.wrappedBuffer(capabilityData)));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(
            () -> LogicalSidedProvider.CLIENTWORLD.<Optional<World>>get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getEntity(this.entityId))
                .filter(PlayerEntity.class::isInstance)
                .map(PlayerEntity.class::cast)
                .map(player -> player.containerMenu)
                .filter(menu -> this.slotIndex < menu.slots.size())
                .map(container -> container.getSlot(this.slotIndex))
                .ifPresent(slot -> slot.getItem().decode(this.capabilityData)));
        return true;
    }
}
