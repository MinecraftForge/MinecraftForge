/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.IPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Means to distribute packets in various ways
 *
 * @see net.minecraftforge.fml.network.simple.SimpleChannel#send(PacketTarget, Object)
 *
 * @param <T>
 */
public class PacketDistributor<T> {
    /**
     * Send to the player specified in the Supplier
     * <br/>
     * {@link #with(Supplier)} Player
     */
    public static final PacketDistributor<ServerPlayerEntity> PLAYER = new PacketDistributor<>(PacketDistributor::playerConsumer, NetworkDirection.PLAY_TO_CLIENT);
    /**
     * Send to everyone in the dimension specified in the Supplier
     * <br/>
     * {@link #with(Supplier)} DimensionType
     */
    public static final PacketDistributor<RegistryKey<World>> DIMENSION = new PacketDistributor<>(PacketDistributor::playerListDimConsumer, NetworkDirection.PLAY_TO_CLIENT);
    /**
     * Send to everyone near the {@link TargetPoint} specified in the Supplier
     * <br/>
     * {@link #with(Supplier)} TargetPoint
     */
    public static final PacketDistributor<TargetPoint> NEAR = new PacketDistributor<>(PacketDistributor::playerListPointConsumer, NetworkDirection.PLAY_TO_CLIENT);
    /**
     * Send to everyone
     * <br/>
     * {@link #noArg()}
     */
    public static final PacketDistributor<Void> ALL = new PacketDistributor<>(PacketDistributor::playerListAll, NetworkDirection.PLAY_TO_CLIENT);
    /**
     * Send to the server (CLIENT to SERVER)
     * <br/>
     * {@link #noArg()}
     */
    public static final PacketDistributor<Void> SERVER = new PacketDistributor<>(PacketDistributor::clientToServer, NetworkDirection.PLAY_TO_SERVER);
    /**
     * Send to all tracking the Entity in the Supplier
     * <br/>
     * {@link #with(Supplier)} Entity
     */
    public static final PacketDistributor<Entity> TRACKING_ENTITY = new PacketDistributor<>(PacketDistributor::trackingEntity, NetworkDirection.PLAY_TO_CLIENT);
    /**
     * Send to all tracking the Entity and Player in the Supplier
     * <br/>
     * {@link #with(Supplier)} Entity
     */
    public static final PacketDistributor<Entity> TRACKING_ENTITY_AND_SELF = new PacketDistributor<>(PacketDistributor::trackingEntityAndSelf, NetworkDirection.PLAY_TO_CLIENT);
    /**
     * Send to all tracking the Chunk in the Supplier
     * <br/>
     * {@link #with(Supplier)} Chunk
     */
    public static final PacketDistributor<Chunk> TRACKING_CHUNK = new PacketDistributor<>(PacketDistributor::trackingChunk, NetworkDirection.PLAY_TO_CLIENT);
    /**
     * Send to the supplied list of NetworkManager instances in the Supplier
     * <br/>
     * {@link #with(Supplier)} List of NetworkManager
     */
    public static final PacketDistributor<List<NetworkManager>> NMLIST = new PacketDistributor<>(PacketDistributor::networkManagerList, NetworkDirection.PLAY_TO_CLIENT);

    public static final class TargetPoint {

        private final ServerPlayerEntity excluded;
        private final double x;
        private final double y;
        private final double z;
        private final double r2;
        private final RegistryKey<World> dim;

        /**
         * A target point with excluded entity
         *
         * @param excluded Entity to exclude
         * @param x X
         * @param y Y
         * @param z Z
         * @param r2 Radius
         * @param dim DimensionType
         */
        public TargetPoint(final ServerPlayerEntity excluded, final double x, final double y, final double z, final double r2, final RegistryKey<World> dim) {
            this.excluded = excluded;
            this.x = x;
            this.y = y;
            this.z = z;
            this.r2 = r2;
            this.dim = dim;
        }

        /**
         * A target point without excluded entity
         * @param x X
         * @param y Y
         * @param z Z
         * @param r2 Radius
         * @param dim DimensionType
         */
        public TargetPoint(final double x, final double y, final double z, final double r2, final RegistryKey<World> dim) {
            this.excluded = null;
            this.x = x;
            this.y = y;
            this.z = z;
            this.r2 = r2;
            this.dim = dim;
        }

        /**
         * Helper to build a TargetPoint without excluded Entity
         * @param x X
         * @param y Y
         * @param z Z
         * @param r2 Radius
         * @param dim DimensionType
         * @return A TargetPoint supplier
         */
        public static Supplier<TargetPoint> p(double x, double y, double z, double r2, RegistryKey<World> dim) {
            TargetPoint tp = new TargetPoint(x, y, z, r2, dim);
            return ()->tp;
        }

    }

    /**
     * A Distributor curried with a specific value instance, for actual dispatch
     *
     * @see net.minecraftforge.fml.network.simple.SimpleChannel#send(PacketTarget, Object)
     *
     */
    public static class PacketTarget {
        private final Consumer<IPacket<?>> packetConsumer;
        private final PacketDistributor<?> distributor;
        PacketTarget(final Consumer<IPacket<?>> packetConsumer, final PacketDistributor<?> distributor) {
            this.packetConsumer = packetConsumer;
            this.distributor = distributor;
        }

        public void send(IPacket<?> packet) {
            packetConsumer.accept(packet);
        }

        public NetworkDirection getDirection() {
            return distributor.direction;
        }

    }

    private final BiFunction<PacketDistributor<T>, Supplier<T>, Consumer<IPacket<?>>> functor;
    private final NetworkDirection direction;

    public PacketDistributor(BiFunction<PacketDistributor<T>, Supplier<T>, Consumer<IPacket<?>>> functor, NetworkDirection direction) {
        this.functor = functor;
        this.direction = direction;
    }

    /**
     * Apply the supplied value to the specific distributor to generate an instance for sending packets to.
     * @param input The input to apply
     * @return A curried instance
     */
    public PacketTarget with(Supplier<T> input) {
        return new PacketTarget(functor.apply(this, input), this);
    }

    /**
     * Apply a no argument value to a distributor to generate an instance for sending packets to.
     *
     * @see #ALL
     * @see #SERVER
     * @return A curried instance
     */
    public PacketTarget noArg() {
        return new PacketTarget(functor.apply(this, ()->null), this);
    }

    private Consumer<IPacket<?>> playerConsumer(final Supplier<ServerPlayerEntity> entityPlayerMPSupplier) {
        return p -> entityPlayerMPSupplier.get().connection.netManager.sendPacket(p);
    }
    private Consumer<IPacket<?>> playerListDimConsumer(final Supplier<RegistryKey<World>> dimensionTypeSupplier) {
        return p->getServer().getPlayerList().func_232642_a_(p, dimensionTypeSupplier.get());
    }

    private Consumer<IPacket<?>> playerListAll(final Supplier<Void> voidSupplier) {
        return p -> getServer().getPlayerList().sendPacketToAllPlayers(p);
    }

    private Consumer<IPacket<?>> clientToServer(final Supplier<Void> voidSupplier) {
        return p -> Minecraft.getInstance().getConnection().sendPacket(p);
    }

    private Consumer<IPacket<?>> playerListPointConsumer(final Supplier<TargetPoint> targetPointSupplier) {
        return p -> {
            final TargetPoint tp = targetPointSupplier.get();
            getServer().getPlayerList().sendToAllNearExcept(tp.excluded, tp.x, tp.y, tp.z, tp.r2, tp.dim, p);
        };
    }

    private Consumer<IPacket<?>> trackingEntity(final Supplier<Entity> entitySupplier) {
        return p-> {
            final Entity entity = entitySupplier.get();
            ((ServerChunkProvider)entity.getEntityWorld().getChunkProvider()).sendToAllTracking(entity, p);
        };
    }

    private Consumer<IPacket<?>> trackingEntityAndSelf(final Supplier<Entity> entitySupplier) {
        return p-> {
            final Entity entity = entitySupplier.get();
            ((ServerChunkProvider)entity.getEntityWorld().getChunkProvider()).sendToTrackingAndSelf(entity, p);
        };
    }

    private Consumer<IPacket<?>> trackingChunk(final Supplier<Chunk> chunkPosSupplier) {
        return p -> {
            final Chunk chunk = chunkPosSupplier.get();
            ((ServerChunkProvider)chunk.getWorld().getChunkProvider()).chunkManager.getTrackingPlayers(chunk.getPos(), false).forEach(e -> e.connection.sendPacket(p));
        };
    }

    private Consumer<IPacket<?>> networkManagerList(final Supplier<List<NetworkManager>> nmListSupplier) {
        return p -> nmListSupplier.get().forEach(nm->nm.sendPacket(p));
    }

    private MinecraftServer getServer() {
        return LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
    }
}
