package net.minecraftforge.network;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.PacketDistributor.PacketTarget;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.network.message.SyncEntityCapabilities;
import net.minecraftforge.network.message.SyncEquipmentSlotCapabilities;
import net.minecraftforge.network.message.SyncSlotCapabilities;
import net.minecraftforge.network.message.SyncBlockEntityCapabilities;

@Mod.EventBusSubscriber
public class ForgeNetwork {

    public static final String PROTOCOL_VERSION = "0.0.1";
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(ForgeMod.ID, "main");
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, () -> PROTOCOL_VERSION,
            clientVersion -> clientVersion.equals(NetworkRegistry.ACCEPTVANILLA)
                    || clientVersion.equals(PROTOCOL_VERSION),
            PROTOCOL_VERSION::equals);

    private static boolean initialized = false;

    public static void init()
    {
        if (initialized)
            throw new IllegalStateException("Forge network already initialized");

        // @formatter:off
        CHANNEL.messageBuilder(SyncSlotCapabilities.class, 0x00, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SyncSlotCapabilities::encode)
            .decoder(SyncSlotCapabilities::decode)
            .consumer(SyncSlotCapabilities::handle)
            .add();

        CHANNEL.messageBuilder(SyncEntityCapabilities.class, 0x01, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SyncEntityCapabilities::encode)
            .decoder(SyncEntityCapabilities::decode)
            .consumer(SyncEntityCapabilities::handle)
            .add();

        CHANNEL.messageBuilder(SyncBlockEntityCapabilities.class, 0x03, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SyncBlockEntityCapabilities::encode)
            .decoder(SyncBlockEntityCapabilities::decode)
            .consumer(SyncBlockEntityCapabilities::handle)
            .add();
        
        CHANNEL.messageBuilder(SyncEquipmentSlotCapabilities.class, 0x04, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SyncEquipmentSlotCapabilities::encode)
            .decoder(SyncEquipmentSlotCapabilities::decode)
            .consumer(SyncEquipmentSlotCapabilities::handle)
            .add();
     // @formatter:on
    }

    @SubscribeEvent
    public static void handleChunkWatch(ChunkWatchEvent.Watch event)
    {
        Chunk chunk = event.getWorld().getChunk(event.getPos().x, event.getPos().z);
        for (TileEntity tileEntity : chunk.getBlockEntities().values())
        {
            sendTileEntityCapabilities(tileEntity, PacketDistributor.PLAYER.with(event::getPlayer), true);
        }
    }

    @SubscribeEvent
    public static void handleTileEntityTick(TickEvent.TileEntityTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END && event.blockEntity.getLevel() != null)
            sendTileEntityCapabilities(event.blockEntity, PacketDistributor.TRACKING_CHUNK
                    .with(() -> event.blockEntity.getLevel().getChunkAt(event.blockEntity.getBlockPos())), false);
    }

    @SubscribeEvent
    public static void handlePlayerStartTracking(PlayerEvent.StartTracking event)
    {
        if (event.getTarget() instanceof LivingEntity)
            sendEntityCapabilities((LivingEntity) event.getTarget(),
                    PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getPlayer()), true);
    }

    @SubscribeEvent
    public static void handleLivingUpdate(LivingUpdateEvent event)
    {
        sendEntityCapabilities(event.getEntityLiving(),
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(event::getEntityLiving), false);
    }

    public static void sendTileEntityCapabilities(TileEntity tileEntity, PacketTarget target, boolean writeAll)
    {
        if (!tileEntity.getLevel().isClientSide() && (writeAll || tileEntity.requiresSync()))
        {
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            tileEntity.encode(capabilityData, writeAll);
            CHANNEL.send(target, new SyncBlockEntityCapabilities(tileEntity.getBlockPos(), capabilityData));
        }
    }

    public static void sendEntityCapabilities(LivingEntity livingEntity, PacketTarget target, boolean writeAll)
    {
        if (!livingEntity.level.isClientSide() && (writeAll || livingEntity.requiresSync()))
        {
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            livingEntity.encode(capabilityData, writeAll);
            CHANNEL.send(target, new SyncEntityCapabilities(livingEntity.getId(), capabilityData));
        }
    }

    public static void sendSlotCapabilities(Slot slot, ServerPlayerEntity target, boolean writeAll)
    {
        if (slot.container == target.inventory)
        {
            for (ItemStack equipmentStack : target.getAllSlots())
            {
                // If the item is equipment we don't need to sync it as Minecraft does
                // that in a separate method (and if we sync it twice the capability wont think
                // it's dirty anymore on the second call).
                if (equipmentStack == slot.getItem())
                {
                    return;
                }
            }
        }

        if (writeAll || slot.getItem().requiresSync())
        {
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            slot.getItem().encode(capabilityData, writeAll);
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> target),
                    new SyncSlotCapabilities(target.getId(), slot.getSlotIndex(), capabilityData));
        }
    }

    public static void sendEquipmentSlotCapabilities(int entityId, EquipmentSlotType equipmentSlotType,
            ItemStack itemStack, PacketTarget target, boolean writeAll)
    {
        if (writeAll || itemStack.requiresSync())
        {
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            itemStack.encode(capabilityData, writeAll);
            CHANNEL.send(target, new SyncEquipmentSlotCapabilities(entityId, equipmentSlotType, capabilityData));
        }
    }
}
