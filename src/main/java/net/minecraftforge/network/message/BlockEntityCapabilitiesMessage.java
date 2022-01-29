package net.minecraftforge.network.message;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

public record BlockEntityCapabilitiesMessage(BlockPos blockPos, FriendlyByteBuf capabilityData) {

    public void encode(FriendlyByteBuf out)
    {
        out.writeBlockPos(this.blockPos);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
        this.capabilityData.release();
    }

    public static BlockEntityCapabilitiesMessage decode(FriendlyByteBuf in)
    {
        return new BlockEntityCapabilitiesMessage(in.readBlockPos(),
            new FriendlyByteBuf(in.readBytes(in.readVarInt())));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get()
            .enqueueWork(() -> LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getBlockEntity(this.blockPos))
                .ifPresent(blockEntity -> blockEntity.readCapabilities(this.capabilityData)))
            .thenRun(this.capabilityData::release);
        return true;
    }
}