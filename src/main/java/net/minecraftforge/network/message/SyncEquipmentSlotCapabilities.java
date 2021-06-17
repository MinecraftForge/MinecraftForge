package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncEquipmentSlotCapabilities {

    private final int entityId;
    private final EquipmentSlotType slotType;
    private final PacketBuffer capabilityData;

    public SyncEquipmentSlotCapabilities(int entityId, EquipmentSlotType slotType, PacketBuffer capabilityData)
    {
        this.entityId = entityId;
        this.slotType = slotType;
        this.capabilityData = capabilityData;
    }

    public void encode(PacketBuffer out)
    {
        out.writeVarInt(this.entityId);
        out.writeEnum(this.slotType);
        out.writeVarInt(this.capabilityData.readableBytes());
        out.writeBytes(this.capabilityData);
    }

    public static SyncEquipmentSlotCapabilities decode(PacketBuffer in)
    {
        int entityId = in.readVarInt();
        EquipmentSlotType slotType = in.readEnum(EquipmentSlotType.class);
        byte[] capabilityData = new byte[in.readVarInt()];
        in.readBytes(capabilityData);
        return new SyncEquipmentSlotCapabilities(entityId, slotType,
            new PacketBuffer(Unpooled.wrappedBuffer(capabilityData)));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(
            () -> LogicalSidedProvider.CLIENTWORLD.<Optional<World>>get(ctx.get().getDirection().getReceptionSide())
                .map(level -> level.getEntity(this.entityId))
                .filter(LivingEntity.class::isInstance)
                .map(LivingEntity.class::cast)
                .ifPresent(livingEntity -> livingEntity.getItemBySlot(this.slotType).decode(this.capabilityData)));
        return true;
    }
}
