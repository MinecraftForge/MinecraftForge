/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/**
 * Means to distribute packets in various ways
 *
 * @see Channel#send(Object, PacketTarget)
 *
 * @param <T>
 */
public record PacketDistributor<T>(BiFunction<PacketDistributor<T>, T, Consumer<Packet<?>>> functor, NetworkDirection<?> direction) {
    /**
     * Send to the player specified in the Supplier
     * <br/>
     * {@link #with(T)} Player
     */
    public static final PacketDistributor<ServerPlayer> PLAYER = new PacketDistributor<>(PacketDistributor::playerConsumer);

    /**
     * Send to everyone in the dimension specified in the Supplier
     * <br/>
     * {@link #with(T)} DimensionType
     */
    public static final PacketDistributor<ResourceKey<Level>> DIMENSION = new PacketDistributor<>(PacketDistributor::playerListDimConsumer);

    /**
     * Send to everyone near the {@link TargetPoint} specified in the Supplier
     * <br/>
     * {@link #with(T)} TargetPoint
     */
    public static final PacketDistributor<TargetPoint> NEAR = new PacketDistributor<>(PacketDistributor::playerListPointConsumer);

    /**
     * Send to everyone
     * <br/>
     * {@link #noArg()}
     */
    public static final PacketDistributor<Void> ALL = new PacketDistributor<>(PacketDistributor::playerListAll);

    /**
     * Send to the server (CLIENT to SERVER)
     * <br/>
     * {@link #noArg()}
     */
    public static final PacketDistributor<Void> SERVER = new PacketDistributor<>(PacketDistributor::clientToServer, NetworkDirection.PLAY_TO_SERVER);

    /**
     * Send to all tracking the Entity in the Supplier
     * <br/>
     * {@link #with(T)} Entity
     */
    public static final PacketDistributor<Entity> TRACKING_ENTITY = new PacketDistributor<>(PacketDistributor::trackingEntity);

    /**
     * Send to all tracking the Entity and Player in the Supplier
     * <br/>
     * {@link #with(T)} Entity
     */
    public static final PacketDistributor<Entity> TRACKING_ENTITY_AND_SELF = new PacketDistributor<>(PacketDistributor::trackingEntityAndSelf);

    /**
     * Send to all tracking the Chunk in the Supplier
     * <br/>
     * {@link #with(T)} Chunk
     */
    public static final PacketDistributor<LevelChunk> TRACKING_CHUNK = new PacketDistributor<>(PacketDistributor::trackingChunk);

    /**
     * Send to the supplied list of NetworkManager instances in the Supplier
     * <br/>
     * {@link #with(T)} List of NetworkManager
     */
    public static final PacketDistributor<List<Connection>> NMLIST = new PacketDistributor<>(PacketDistributor::networkManagerList);

    public static record TargetPoint(@Nullable ServerPlayer excluded, double x, double y, double z, double r2, ResourceKey<Level> dim) {
        /**
         * A target point without excluded entity
         */
        public TargetPoint(double x, double y, double z, double r2, ResourceKey<Level> dim) {
            this(null, x, y, z, r2, dim);
        }
    }

    /**
     * A Distributor curried with a specific value instance, for actual dispatch
     *
     * @see Channel#send(Object, PacketTarget)
     */
    public record PacketTarget(Consumer<Packet<?>> packetConsumer, NetworkDirection<?> direction) {
        public void send(Packet<?> packet) {
            packetConsumer.accept(packet);
        }
    }

    public PacketDistributor(BiFunction<PacketDistributor<T>, T, Consumer<Packet<?>>> functor) {
        this(functor, NetworkDirection.PLAY_TO_CLIENT);
    }

    /**
     * Apply the supplied value to the specific distributor to generate an instance for sending packets to.
     * @param input The input to apply
     * @return A curried instance
     */
    public PacketTarget with(T input) {
        return new PacketTarget(functor.apply(this, input), this.direction());
    }

    /**
     * Apply a no argument value to a distributor to generate an instance for sending packets to.
     *
     * @see #ALL
     * @see #SERVER
     * @return A curried instance
     */
    public PacketTarget noArg() {
        return new PacketTarget(functor.apply(this, null), this.direction());
    }

    private Consumer<Packet<?>> playerConsumer(ServerPlayer player) {
        return p -> player.connection.getConnection().send(p);
    }

    private Consumer<Packet<?>> playerListDimConsumer(ResourceKey<Level> dimension) {
        return p -> getServer().getPlayerList().broadcastAll(p, dimension);
    }

    private Consumer<Packet<?>> playerListAll(Void ignored) {
        return p -> getServer().getPlayerList().broadcastAll(p);
    }

    private Consumer<Packet<?>> clientToServer(Void ignored) {
        return p -> Minecraft.getInstance().getConnection().send(p);
    }

    private Consumer<Packet<?>> playerListPointConsumer(TargetPoint tp) {
        return p -> getServer().getPlayerList().broadcast(tp.excluded, tp.x, tp.y, tp.z, tp.r2, tp.dim, p);
    }

    private Consumer<Packet<?>> trackingEntity(Entity entity) {
        return p -> ((ServerChunkCache)entity.getCommandSenderWorld().getChunkSource()).broadcast(entity, p);
    }

    private Consumer<Packet<?>> trackingEntityAndSelf(Entity entity) {
        return p -> ((ServerChunkCache)entity.getCommandSenderWorld().getChunkSource()).broadcastAndSend(entity, p);
    }

    private Consumer<Packet<?>> trackingChunk(LevelChunk chunk) {
        return p -> ((ServerChunkCache)chunk.getLevel().getChunkSource()).chunkMap.getPlayers(chunk.getPos(), false).forEach(e -> e.connection.send(p));
    }

    private Consumer<Packet<?>> networkManagerList(List<Connection> connections) {
        return p -> connections.forEach(nm->nm.send(p));
    }

    private MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }
}
