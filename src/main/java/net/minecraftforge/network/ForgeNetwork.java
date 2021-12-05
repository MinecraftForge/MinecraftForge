package net.minecraftforge.network;

import java.util.Set;
import java.util.stream.Stream;

import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerPlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.message.BlockEntityCapabilitiesMessage;
import net.minecraftforge.network.message.EntityCapabilitiesMessage;
import net.minecraftforge.network.message.EquipmentSlotCapabilitiesMessage;
import net.minecraftforge.network.message.SlotCapabilitiesMessage;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeNetwork {

    public static final String PROTOCOL_VERSION = "0.0.1";
    public static final ResourceLocation GAME_CHANNEL_NAME = new ResourceLocation("forge", "game");
    public static final SimpleChannel gameChannel = NetworkRegistry.newSimpleChannel(GAME_CHANNEL_NAME,
        () -> PROTOCOL_VERSION, NetworkRegistry.acceptMissingOr(PROTOCOL_VERSION), PROTOCOL_VERSION::equals);

    private static boolean initialized = false;

    public static void initialize()
    {
        if (initialized)
            throw new IllegalStateException("Forge network already initialized");

        gameChannel.messageBuilder(SlotCapabilitiesMessage.class, 0x00, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(SlotCapabilitiesMessage::encode)
            .decoder(SlotCapabilitiesMessage::decode)
            .consumer(SlotCapabilitiesMessage::handle)
            .add();

        gameChannel.messageBuilder(EntityCapabilitiesMessage.class, 0x01, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(EntityCapabilitiesMessage::encode)
            .decoder(EntityCapabilitiesMessage::decode)
            .consumer(EntityCapabilitiesMessage::handle)
            .add();

        gameChannel.messageBuilder(BlockEntityCapabilitiesMessage.class, 0x03, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(BlockEntityCapabilitiesMessage::encode)
            .decoder(BlockEntityCapabilitiesMessage::decode)
            .consumer(BlockEntityCapabilitiesMessage::handle)
            .add();

        gameChannel.messageBuilder(EquipmentSlotCapabilitiesMessage.class, 0x04, NetworkDirection.PLAY_TO_CLIENT)
            .encoder(EquipmentSlotCapabilitiesMessage::encode)
            .decoder(EquipmentSlotCapabilitiesMessage::decode)
            .consumer(EquipmentSlotCapabilitiesMessage::handle)
            .add();
    }

    private static void broadcast(Object message, Stream<Connection> players)
    {
        broadcast(gameChannel.toVanillaPacket(message, NetworkDirection.PLAY_TO_CLIENT), players);
    }

    private static void broadcast(Packet<?> packet, Stream<Connection> connections)
    {
        connections.forEach(connection -> sendIfPresent(packet, connection));
    }

    private static void sendIfPresent(Packet<?> packet, Connection connection)
    {
        if (gameChannel.isRemotePresent(connection))
            connection.send(packet);
    }

    /**
     * Sends the specified block entity's caps to a stream of connections.
     * 
     * @param blockEntity - the block entity for whom's caps to send
     * @param writeAll    - <code>true</code> to write all cap data,
     *                    <code>false</code> to write only dirty data.
     * @param connections - the connections to send the block entity's caps to
     */
    public static void sendBlockEntityCapabilities(BlockEntity blockEntity, boolean writeAll,
        Stream<Connection> connections)
    {
        if (!blockEntity.hasLevel() || blockEntity.getLevel().isClientSide())
            return;

        FriendlyByteBuf capabilityData = new FriendlyByteBuf(Unpooled.buffer());
        blockEntity.writeCapabilities(capabilityData, writeAll);
        if (!capabilityData.isReadable())
            return;

        broadcast(new BlockEntityCapabilitiesMessage(blockEntity.getBlockPos(), capabilityData), connections);
    }

    /**
     * Sends the specified block entity's caps to everyone tracking it.
     * 
     * @param blockEntity - the block entity for whom's caps to send
     * @param writeAll    - <code>true</code> to write all cap data,
     *                    <code>false</code> to write only dirty data.
     */
    public static void sendBlockEntityCapabilities(BlockEntity blockEntity, boolean writeAll)
    {
        if (!blockEntity.hasLevel() || blockEntity.getLevel().isClientSide())
            return;

        FriendlyByteBuf capabilityData = new FriendlyByteBuf(Unpooled.buffer());
        blockEntity.writeCapabilities(capabilityData, writeAll);
        if (!capabilityData.isReadable())
            return;

        ServerChunkCache chunkSource = ((ServerLevel) blockEntity.getLevel()).getChunkSource();
        broadcast(new BlockEntityCapabilitiesMessage(blockEntity.getBlockPos(), capabilityData),
            chunkSource.chunkMap.getPlayers(new ChunkPos(blockEntity.getBlockPos()), false)
                .stream()
                .map(player -> player.connection.getConnection()));
    }

    /**
     * Sends the specified entity's caps to all tracking entities including itself
     * in the case of it being a player.
     * 
     * @param entity   - the entity for whom's caps you'resending
     * @param writeAll - <code>true</code> to write all cap data, <code>false</code>
     *                 to write only dirty data.
     */
    public static void sendEntityCapabilities(Entity entity, boolean writeAll)
    {
        sendEntityCapabilities(entity, writeAll, true, getTrackingConnections(entity));
    }

    /**
     * Sends the specified entity's caps to a set amount of players.
     * 
     * @param entity      - the entity for whom's caps you're sending
     * @param writeAll    - <code>true</code> to write all cap data,
     *                    <code>false</code> to write only dirty data.
     * @param sendToSelf  - send to itself (if the entity is a player)
     * @param connections - the players to send the entity's caps to
     */
    public static void sendEntityCapabilities(Entity entity, boolean writeAll, boolean sendToSelf,
        Stream<Connection> connections)
    {
        if (entity.level.isClientSide())
            return;

        FriendlyByteBuf capabilityData = new FriendlyByteBuf(Unpooled.buffer());
        entity.writeCapabilities(capabilityData, writeAll);
        if (!capabilityData.isReadable())
            return;

        Packet<?> packet = gameChannel.toVanillaPacket(
            new EntityCapabilitiesMessage(entity.getId(), capabilityData), NetworkDirection.PLAY_TO_CLIENT);
        if (sendToSelf && entity instanceof ServerPlayer player)
        {
            sendIfPresent(packet, player.connection.getConnection());
        }
        broadcast(packet, connections);
    }

    /**
     * Send the capabilities associated with the item in the specified slot.
     * 
     * @param slot     - the slot containing the item whom's caps you're sending
     * @param target   - the player to send them to
     * @param writeAll - <code>true</code> to write all cap data, <code>false</code>
     *                 to write only dirty data.
     */
    public static void sendSlotCapabilities(AbstractContainerMenu menu, int slotIndex, ItemStack itemStack,
        ServerPlayer target, boolean writeAll)
    {
        if (menu == target.inventoryMenu)
        {
            for (ItemStack equipmentStack : target.getAllSlots())
            {
                // If the item is equipment we don't need to sync it as Minecraft does
                // that in a separate method (and if we sync it twice the capability wont think
                // it's dirty anymore on the second call).
                if (equipmentStack == itemStack)
                {
                    return;
                }
            }
        }

        FriendlyByteBuf capabilityData = new FriendlyByteBuf(Unpooled.buffer());
        itemStack.writeCapabilities(capabilityData, writeAll);
        if (!capabilityData.isReadable())
            return;

        broadcast(new SlotCapabilitiesMessage(target.getId(), slotIndex, capabilityData),
            Stream.of(target.connection.getConnection()));
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
    public static void sendEquipmentSlotCapabilities(LivingEntity livingEntity, EquipmentSlot equipmentSlotType,
        ItemStack itemStack, boolean writeAll)
    {
        sendEquipmentSlotCapabilities(livingEntity, equipmentSlotType, itemStack, writeAll, true,
            getTrackingConnections(livingEntity));
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
    public static void sendEquipmentSlotCapabilities(LivingEntity livingEntity, EquipmentSlot equipmentSlotType,
        ItemStack itemStack, boolean writeAll, boolean sendToSelf, Stream<Connection> players)
    {
        if (livingEntity.level.isClientSide())
            return;

        FriendlyByteBuf capabilityData = new FriendlyByteBuf(Unpooled.buffer());
        itemStack.writeCapabilities(capabilityData, writeAll);
        if (!capabilityData.isReadable())
            return;

        Packet<?> packet = gameChannel.toVanillaPacket(
            new EquipmentSlotCapabilitiesMessage(livingEntity.getId(), equipmentSlotType, capabilityData),
            NetworkDirection.PLAY_TO_CLIENT);
        if (sendToSelf && livingEntity instanceof ServerPlayer player)
        {
            sendIfPresent(packet, player.connection.getConnection());
        }
        broadcast(packet, players);
    }

    public static Stream<Connection> getTrackingConnections(Entity entity)
    {
        if (entity.level instanceof ServerLevel level)
        {
            ServerChunkCache chunkSource = level.getChunkSource();
            Set<ServerPlayerConnection> players = chunkSource.chunkMap.getSeenBy(entity);
            if (players != null)
            {
                return players.stream()
                    .map(ServerPlayerConnection::getPlayer)
                    .map(player -> player.connection.getConnection());
            }
        }
        return Stream.empty();
    }
}