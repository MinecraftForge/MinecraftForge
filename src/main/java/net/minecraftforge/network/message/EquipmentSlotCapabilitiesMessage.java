package net.minecraftforge.network.message;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.network.NetworkEvent;

public record EquipmentSlotCapabilitiesMessage(int entityId, EquipmentSlot slot, FriendlyByteBuf capabilityData) {

    public void encode(FriendlyByteBuf out)
    {
        out.writeVarInt(this.entityId);
        out.writeEnum(this.slot);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
        this.capabilityData.release();
    }

    public static EquipmentSlotCapabilitiesMessage decode(FriendlyByteBuf in)
    {
        return new EquipmentSlotCapabilitiesMessage(in.readVarInt(), in.readEnum(EquipmentSlot.class),
            new FriendlyByteBuf(in.readBytes(in.readVarInt())));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get()
            .enqueueWork(() -> LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getEntity(this.entityId))
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .ifPresent(livingEntity -> livingEntity.getItemBySlot(this.slot).readCapabilities(this.capabilityData)))
            .thenRun(this.capabilityData::release);
        return true;
    }
}