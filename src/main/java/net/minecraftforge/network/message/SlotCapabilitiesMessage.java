package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class SlotCapabilitiesMessage {

    private final int entityId;
    private final int slotIndex;
    private final FriendlyByteBuf capabilityData;

    public SlotCapabilitiesMessage(int entityId, int slotIndex, FriendlyByteBuf capabilityData)
    {
        this.entityId = entityId;
        this.slotIndex = slotIndex;
        this.capabilityData = capabilityData;
    }

    public void encode(FriendlyByteBuf out)
    {
        out.writeVarInt(this.entityId);
        out.writeShort(this.slotIndex);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
        this.capabilityData.release();
    }

    public static SlotCapabilitiesMessage decode(FriendlyByteBuf in)
    {
        return new SlotCapabilitiesMessage(in.readVarInt(), in.readVarInt(),
            new FriendlyByteBuf(in.readBytes(in.readVarInt())));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(
            () -> LogicalSidedProvider.CLIENTWORLD.<Optional<Level>>get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getEntity(this.entityId))
                .filter(Player.class::isInstance)
                .map(Player.class::cast)
                .map(player -> player.containerMenu)
                .filter(menu -> this.slotIndex < menu.slots.size())
                .map(container -> container.getSlot(this.slotIndex))
                .ifPresent(slot -> slot.getItem().read(this.capabilityData)))
            .thenRun(this.capabilityData::release);
        return true;
    }
}