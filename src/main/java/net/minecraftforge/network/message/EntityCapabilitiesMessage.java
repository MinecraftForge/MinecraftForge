package net.minecraftforge.network.message;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

public class EntityCapabilitiesMessage {

    private final int entityId;
    private final FriendlyByteBuf capabilityData;

    public EntityCapabilitiesMessage(int entityId, FriendlyByteBuf capabilityData)
    {
        this.entityId = entityId;
        this.capabilityData = capabilityData;
    }

    public void encode(FriendlyByteBuf out)
    {
        out.writeVarInt(this.entityId);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
        this.capabilityData.release();
    }

    public static EntityCapabilitiesMessage decode(FriendlyByteBuf in)
    {
        return new EntityCapabilitiesMessage(in.readVarInt(),
            new FriendlyByteBuf(in.readBytes(in.readVarInt())));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get()
            .enqueueWork(() -> LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getEntity(this.entityId))
                .ifPresent(entity -> entity.readCapabilities(this.capabilityData)))
            .thenRun(this.capabilityData::release);
        return true;
    }
}