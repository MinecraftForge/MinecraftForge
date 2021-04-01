package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
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

    public static void encode(SyncBlockEntityCapabilities msg, PacketBuffer out)
    {
        out.writeBlockPos(msg.blockPos);
        out.writeVarInt(msg.capabilityData.readableBytes());
        out.writeBytes(msg.capabilityData);
    }

    public static SyncBlockEntityCapabilities decode(PacketBuffer in)
    {
        BlockPos blockPos = in.readBlockPos();
        byte[] capabilityData = new byte[in.readVarInt()];
        in.readBytes(capabilityData);
        SyncBlockEntityCapabilities msg = new SyncBlockEntityCapabilities(blockPos, new PacketBuffer(Unpooled.wrappedBuffer(capabilityData)));
        return msg;
    }

    public static boolean handle(SyncBlockEntityCapabilities msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Optional<World> level = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
            TileEntity tileEntity = level.flatMap(w -> Optional.ofNullable(w.getBlockEntity(msg.blockPos))).orElse(null);
            if (tileEntity == null)
            {
                return;
            }
            tileEntity.decode(msg.capabilityData);
        });
        return true;
    }
}
