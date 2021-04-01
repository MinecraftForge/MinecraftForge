package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
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

    public static void encode(SyncEquipmentSlotCapabilities msg, PacketBuffer out)
    {
        out.writeVarInt(msg.entityId);
        out.writeEnum(msg.slotType);
        out.writeVarInt(msg.capabilityData.readableBytes());
        out.writeBytes(msg.capabilityData);
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

    public static boolean handle(SyncEquipmentSlotCapabilities msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Optional<World> level = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
            Entity entity = level.map(w -> w.getEntity(msg.entityId)).orElse(null);
            if (!(entity instanceof LivingEntity))
            {
                return;
            }

            ((LivingEntity) entity).getItemBySlot(msg.slotType).decode(msg.capabilityData);
        });
        return true;
    }
}
