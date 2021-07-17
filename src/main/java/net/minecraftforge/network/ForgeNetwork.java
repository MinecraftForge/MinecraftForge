package net.minecraftforge.network;

import java.util.Set;
import java.util.stream.Stream;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.network.message.SyncBlockEntityCapabilities;
import net.minecraftforge.network.message.SyncEntityCapabilities;
import net.minecraftforge.network.message.SyncEquipmentSlotCapabilities;
import net.minecraftforge.network.message.SyncSlotCapabilities;

public class ForgeNetwork {

    public static final String PROTOCOL_VERSION = "0.0.1";
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(ForgeMod.ID, "main");
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(CHANNEL_NAME, () -> PROTOCOL_VERSION,
        NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION), PROTOCOL_VERSION::equals);

    private static boolean initialized = false;

    public static void init()
    {
        if (initialized)
            throw new IllegalStateException("Forge network already initialized");

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
    }

    private static void broadcast(Object message, ServerPlayerEntity... players)
    {
        broadcast(CHANNEL.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT), players);
    }

    private static void broadcast(IPacket<?> packet, ServerPlayerEntity... players)
    {
        for (ServerPlayerEntity player : players)
        {
            sendIfPresent(packet, player.connection.connection);
        }
    }

    private static void broadcast(Object message, Stream<NetworkManager> connections)
    {
        IPacket<?> packet = CHANNEL.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT);
        connections.forEach(connection -> sendIfPresent(packet, connection));
    }

    private static void sendIfPresent(IPacket<?> packet, NetworkManager connection)
    {
        if (CHANNEL.isRemotePresent(connection))
            connection.send(packet);
    }

    /**
     * Sends the specified block entity's caps to a set amount of players.
     * 
     * @param blockEntity - the block entity for whom's caps to send
     * @param writeAll    - <code>true</code> to write all cap data,
     *                    <code>false</code> to write only dirty data.
     * @param players     - the players to send the block entity's caps to
     */
    public static void sendBlockEntityCapabilities(TileEntity blockEntity, boolean writeAll,
        ServerPlayerEntity... players)
    {
        if (blockEntity.hasLevel() && !blockEntity.getLevel().isClientSide()
            && (writeAll || blockEntity.requiresSync()))
        {
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            blockEntity.encode(capabilityData, writeAll);
            broadcast(new SyncBlockEntityCapabilities(blockEntity.getBlockPos(), capabilityData), players);
        }
    }

    /**
     * Sends the specified block entity's caps to everyone tracking it.
     * 
     * @param blockEntity - the block entity for whom's caps to send
     * @param writeAll    - <code>true</code> to write all cap data,
     *                    <code>false</code> to write only dirty data.
     */
    public static void sendBlockEntityCapabilities(TileEntity blockEntity, boolean writeAll)
    {
        if (blockEntity.hasLevel() && !blockEntity.getLevel().isClientSide()
            && (writeAll || blockEntity.requiresSync()))
        {
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            blockEntity.encode(capabilityData, false);
            ServerChunkProvider chunkProvider = ((ServerWorld) blockEntity.getLevel()).getChunkSource();
            broadcast(new SyncBlockEntityCapabilities(blockEntity.getBlockPos(), capabilityData),
                chunkProvider.chunkMap.getPlayers(new ChunkPos(blockEntity.getBlockPos()), writeAll)
                    .map(player -> player.connection.connection));
        }
    }

    /**
     * Sends the specified entity's caps to all tracking entities including itself
     * in the case of it being a player.
     * 
     * @param livingEntity - the entity for whom's caps you'resending
     * @param writeAll     - <code>true</code> to write all cap data,
     *                     <code>false</code> to write only dirty data.
     */
    public static void sendEntityCapabilities(LivingEntity livingEntity, boolean writeAll)
    {
        if (livingEntity.level instanceof ServerWorld)
        {
            ServerChunkProvider chunkProvider = ((ServerWorld) livingEntity.level).getChunkSource();
            Set<ServerPlayerEntity> players = chunkProvider.chunkMap.getSeenBy(livingEntity);
            sendEntityCapabilities(livingEntity, writeAll, true,
                players == null ? new ServerPlayerEntity[0] : players.toArray(new ServerPlayerEntity[0]));
        }
    }

    /**
     * Sends the specified entity's caps to a set amount of players.
     * 
     * @param livingEntity - the entity for whom's caps you're sending
     * @param writeAll     - <code>true</code> to write all cap data,
     *                     <code>false</code> to write only dirty data.
     * @param sendToSelf   - send to itself (if the entity is a player)
     * @param players      - the players to send the entity's caps to
     */
    public static void sendEntityCapabilities(LivingEntity livingEntity, boolean writeAll, boolean sendToSelf,
        ServerPlayerEntity... players)
    {
        if (!livingEntity.level.isClientSide() && (writeAll || livingEntity.requiresSync()))
        {
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            livingEntity.encode(capabilityData, writeAll);
            IPacket<?> packet = CHANNEL.toVanillaPacket(
                new SyncEntityCapabilities(livingEntity.getId(), capabilityData), NetworkDirection.PLAY_TO_CLIENT);
            if (sendToSelf && livingEntity instanceof ServerPlayerEntity)
            {
                sendIfPresent(packet, ((ServerPlayerEntity) livingEntity).connection.connection);
            }
            broadcast(packet, players);
        }
    }

    /**
     * Send the capabilities associated with the item in the specified slot.
     * 
     * @param slot     - the slot containing the item whom's caps you're sending
     * @param target   - the player to send them to
     * @param writeAll - <code>true</code> to write all cap data, <code>false</code>
     *                 to write only dirty data.
     */
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
            broadcast(new SyncSlotCapabilities(target.getId(), slot.getSlotIndex(), capabilityData), target);
        }
    }

    /**
     * Sends the specified entity's equipment caps to all tracking entities
     * including itself in the case of it being a player.
     * 
     * @param livingEntity      - the entity for whom's equipment caps you're
     *                          sending
     * @param equipmentSlotType - the equipment slot
     * @param itemStack         - the equipment item stack
     * @param writeAll          - <code>true</code> to write all cap data,
     *                          <code>false</code> to write only dirty data.
     */
    public static void sendEquipmentSlotCapabilities(LivingEntity livingEntity, EquipmentSlotType equipmentSlotType,
        ItemStack itemStack, boolean writeAll)
    {
        if (livingEntity.level instanceof ServerWorld)
        {
            ServerChunkProvider chunkProvider = ((ServerWorld) livingEntity.level).getChunkSource();
            Set<ServerPlayerEntity> players = chunkProvider.chunkMap.getSeenBy(livingEntity);
            sendEquipmentSlotCapabilities(livingEntity, equipmentSlotType, itemStack, writeAll, true,
                players == null ? new ServerPlayerEntity[0] : players.toArray(new ServerPlayerEntity[0]));
        }
    }

    /**
     * Sends the specified entity's equipment caps to a set amount of players.
     * 
     * @param livingEntity      - the entity for whom's equipment caps you're
     *                          sending
     * @param equipmentSlotType - the equipment slot
     * @param itemStack         - the equipment item stack
     * @param writeAll          - <code>true</code> to write all cap data,
     *                          <code>false</code> to write only dirty data.
     * @param sendToSelf        - send to itself (if the entity is a player)
     * @param players           - the players to send the entity's equipment caps to
     */
    public static void sendEquipmentSlotCapabilities(LivingEntity livingEntity, EquipmentSlotType equipmentSlotType,
        ItemStack itemStack, boolean writeAll, boolean sendToSelf, ServerPlayerEntity... players)
    {
        if (!livingEntity.level.isClientSide() && (writeAll || itemStack.requiresSync()))
        {
            PacketBuffer capabilityData = new PacketBuffer(Unpooled.buffer());
            itemStack.encode(capabilityData, writeAll);
            IPacket<?> packet = CHANNEL.toVanillaPacket(
                new SyncEquipmentSlotCapabilities(livingEntity.getId(), equipmentSlotType, capabilityData),
                NetworkDirection.PLAY_TO_CLIENT);
            if (sendToSelf && livingEntity instanceof ServerPlayerEntity)
            {
                sendIfPresent(packet, ((ServerPlayerEntity) livingEntity).connection.connection);
            }
            broadcast(packet, players);
        }
    }
}
