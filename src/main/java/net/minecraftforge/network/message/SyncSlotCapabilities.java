package net.minecraftforge.network.message;

import java.util.Optional;
import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncSlotCapabilities {

    private final int entityId;
    private final int slotIndex;
    private final PacketBuffer capabilityData;

    public SyncSlotCapabilities(int entityId, int slotIndex, PacketBuffer capabilityData)
    {
        this.entityId = entityId;
        this.slotIndex = slotIndex;
        this.capabilityData = capabilityData;
    }

    public static void encode(SyncSlotCapabilities msg, PacketBuffer out)
    {
        out.writeVarInt(msg.entityId);
        out.writeShort(msg.slotIndex);
        out.writeVarInt(msg.capabilityData.readableBytes());
        out.writeBytes(msg.capabilityData);
    }

    public static SyncSlotCapabilities decode(PacketBuffer in)
    {
        int entityId = in.readVarInt();
        int slotIndex = in.readShort();
        byte[] capabilityData = new byte[in.readVarInt()];
        in.readBytes(capabilityData);
        return new SyncSlotCapabilities(entityId, slotIndex,
                new PacketBuffer(Unpooled.wrappedBuffer(capabilityData)));
    }

    public static boolean handle(SyncSlotCapabilities msg, Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Optional<World> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
            Entity entity = world.map(w -> w.getEntityByID(msg.entityId)).orElse(null);
            if (entity == null || !(entity instanceof PlayerEntity))
            {
                return;
            }

            Container container = ((PlayerEntity) entity).openContainer;
            if (msg.slotIndex < container.inventorySlots.size())
            {
                Slot slot = container.getSlot(msg.slotIndex);
                slot.getStack().decode(msg.capabilityData);
            }
        });
        return true;
    }
}
