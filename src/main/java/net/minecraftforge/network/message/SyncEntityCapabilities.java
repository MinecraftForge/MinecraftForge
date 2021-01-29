package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
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

    public static void encode(SyncEntityCapabilities msg, PacketBuffer out)
    {
        out.writeVarInt(msg.entityId);
        out.writeVarInt(msg.capabilityData.readableBytes());
        out.writeBytes(msg.capabilityData);
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

    public static boolean handle(SyncEntityCapabilities msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Optional<World> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
            Entity entity = world.map(w -> w.getEntityByID(msg.entityId)).orElse(null);
            if (entity == null)
            {
                return;
            }
            entity.decode(msg.capabilityData);
        });
        return true;
    }
}
