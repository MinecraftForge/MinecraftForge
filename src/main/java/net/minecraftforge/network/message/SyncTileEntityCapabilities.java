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

public class SyncTileEntityCapabilities {

    private final BlockPos blockPos;
    private final PacketBuffer capabilityData;

    public SyncTileEntityCapabilities(BlockPos blockPos, PacketBuffer capabilityData)
    {
        this.blockPos = blockPos;
        this.capabilityData = capabilityData;
    }

    public static void encode(SyncTileEntityCapabilities msg, PacketBuffer out)
    {
        out.writeBlockPos(msg.blockPos);
        out.writeVarInt(msg.capabilityData.readableBytes());
        out.writeBytes(msg.capabilityData);
    }

    public static SyncTileEntityCapabilities decode(PacketBuffer in)
    {
        BlockPos blockPos = in.readBlockPos();
        byte[] capabilityData = new byte[in.readVarInt()];
        in.readBytes(capabilityData);
        SyncTileEntityCapabilities msg = new SyncTileEntityCapabilities(blockPos, new PacketBuffer(Unpooled.wrappedBuffer(capabilityData)));
        return msg;
    }

    public static boolean handle(SyncTileEntityCapabilities msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Optional<World> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
            TileEntity tileEntity = world.flatMap(w -> Optional.ofNullable(w.getTileEntity(msg.blockPos))).orElse(null);
            if (tileEntity == null)
            {
                return;
            }
            tileEntity.decode(msg.capabilityData);
        });
        return true;
    }
}
