package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncEntityCapabilities {

    private final int entityId;
    private final PacketBuffer capabilityData;

    public SyncEntityCapabilities(int entityId, PacketBuffer capabilityData)
    {
        this.entityId = entityId;
        this.capabilityData = capabilityData;
    }

    public void encode(PacketBuffer out)
    {
        out.writeVarInt(this.entityId);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
    }

    public static SyncEntityCapabilities decode(PacketBuffer in)
    {
        int entityId = in.readVarInt();
        byte[] capabilityData = new byte[in.readVarInt()];
        in.readBytes(capabilityData);
        SyncEntityCapabilities msg = new SyncEntityCapabilities(entityId,
            new PacketBuffer(Unpooled.wrappedBuffer(capabilityData)));
        return msg;
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(
            () -> LogicalSidedProvider.CLIENTWORLD.<Optional<World>>get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getEntity(this.entityId))
                .ifPresent(entity -> entity.decode(this.capabilityData)));
        return true;
    }
}
