package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncBlockEntityCapabilities {

    private final BlockPos blockPos;
    private final PacketBuffer capabilityData;

    public SyncBlockEntityCapabilities(BlockPos blockPos, PacketBuffer capabilityData)
    {
        this.blockPos = blockPos;
        this.capabilityData = capabilityData;
    }

    public void encode(PacketBuffer out)
    {
        out.writeBlockPos(this.blockPos);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
    }

    public static SyncBlockEntityCapabilities decode(PacketBuffer in)
    {
        BlockPos blockPos = in.readBlockPos();
        byte[] capabilityData = new byte[in.readVarInt()];
        in.readBytes(capabilityData);
        SyncBlockEntityCapabilities msg = new SyncBlockEntityCapabilities(blockPos,
            new PacketBuffer(Unpooled.wrappedBuffer(capabilityData)));
        return msg;
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(
            () -> LogicalSidedProvider.CLIENTWORLD.<Optional<World>>get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getBlockEntity(this.blockPos))
                .ifPresent(blockEntity -> blockEntity.decode(this.capabilityData)));
        return true;
    }
}
