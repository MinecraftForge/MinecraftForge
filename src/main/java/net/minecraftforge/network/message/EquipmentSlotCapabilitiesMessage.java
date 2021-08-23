package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class EquipmentSlotCapabilitiesMessage {

    private final int entityId;
    private final EquipmentSlot slot;
    private final FriendlyByteBuf capabilityData;

    public EquipmentSlotCapabilitiesMessage(int entityId, EquipmentSlot slot, FriendlyByteBuf capabilityData)
    {
        this.entityId = entityId;
        this.slot = slot;
        this.capabilityData = capabilityData;
    }

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
        ctx.get().enqueueWork(
            () -> LogicalSidedProvider.CLIENTWORLD.<Optional<Level>>get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getEntity(this.entityId))
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .ifPresent(livingEntity -> livingEntity.getItemBySlot(this.slot).read(this.capabilityData)))
            .thenRun(this.capabilityData::release);
        return true;
    }
}