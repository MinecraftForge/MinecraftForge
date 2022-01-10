/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.network.filters;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundRemoveMobEffectPacket;
import net.minecraft.network.protocol.game.ClientboundUpdateMobEffectPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Replacement for vanilla packets {@link ClientboundUpdateMobEffectPacket} and {@link ClientboundRemoveMobEffectPacket}
 * to remove 255 ID limit
 */
public class ExtendedMobEffectChannel
{

    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation("forge", "mob_effects");
    private static final String VERSION = "1.0";

    private static SimpleChannel CHANNEL;

    public static void register()
    {
        Predicate<String> versionCheck = NetworkRegistry.acceptMissingOr(VERSION);
        CHANNEL = NetworkRegistry.ChannelBuilder.named(CHANNEL_NAME)
                .networkProtocolVersion(() -> VERSION)
                .clientAcceptedVersions(versionCheck)
                .serverAcceptedVersions(versionCheck)
                .simpleChannel();

        CHANNEL.messageBuilder(UpdateMobEffect.class, 0, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(UpdateMobEffect::write)
                .decoder(UpdateMobEffect::read)
                .consumer(UpdateMobEffect::handle)
                .add();

        CHANNEL.messageBuilder(RemoveMobEffect.class, 1, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(RemoveMobEffect::write)
                .decoder(RemoveMobEffect::read)
                .consumer(RemoveMobEffect::handle)
                .add();
    }

    public static boolean shouldBeUsed(@Nonnull Connection connection)
    {
        var connectionData = NetworkHooks.getConnectionData(connection);
        return connectionData != null && connectionData.getChannels().containsKey(CHANNEL_NAME);
    }

    public static void registerHandlers(ImmutableMap.Builder<Class<? extends Packet<?>>, BiConsumer<Packet<?>, List<? super Packet<?>>>> builder)
    {
        builder
                .put(ClientboundUpdateMobEffectPacket.class, (packet, out) -> applyToPacket(((ClientboundUpdateMobEffectPacket) packet), out))
                .put(ClientboundRemoveMobEffectPacket.class, (packet, out) -> applyToPacket(((ClientboundRemoveMobEffectPacket) packet), out));
    }

    private static void applyToPacket(ClientboundUpdateMobEffectPacket vanillaPacket, List<? super Packet<?>> out)
    {
        out.add(CHANNEL.toVanillaPacket(UpdateMobEffect.fromVanillaPacket(vanillaPacket), NetworkDirection.PLAY_TO_CLIENT));
    }

    private static void applyToPacket(ClientboundRemoveMobEffectPacket vanillaPacket, List<? super Packet<?>> out)
    {
        out.add(CHANNEL.toVanillaPacket(RemoveMobEffect.fromVanillaPacket(vanillaPacket), NetworkDirection.PLAY_TO_CLIENT));
    }

    public record UpdateMobEffect(int entityId, MobEffect effect, int amplifier, int durationTicks, byte flags)
    {

        private static final int FLAG_AMBIENT = 1;
        private static final int FLAG_VISIBLE = 2;
        private static final int FLAG_SHOW_ICON = 4;

        private static final int MAX_EFFECT_TICKS = 32767;

        public static UpdateMobEffect fromVanillaPacket(ClientboundUpdateMobEffectPacket vanillaPacket)
        {
            return new UpdateMobEffect(
                    vanillaPacket.getEntityId(),
                    Objects.requireNonNull(vanillaPacket.effect),
                    vanillaPacket.getEffectAmplifier(),
                    vanillaPacket.getEffectDurationTicks(),
                    getFlags(vanillaPacket.isEffectAmbient(), vanillaPacket.isEffectVisible(), vanillaPacket.effectShowsIcon())
            );
        }

        private static byte getFlags(boolean isAmbient, boolean isVisible, boolean showIcon)
        {
            byte flags = 0;
            if (isAmbient)
            {
                flags |= FLAG_AMBIENT;
            }
            if (isVisible)
            {
                flags |= FLAG_VISIBLE;
            }
            if (showIcon)
            {
                flags |= FLAG_SHOW_ICON;
            }
            return flags;
        }

        public void write(FriendlyByteBuf buf)
        {
            buf.writeVarInt(entityId);
            buf.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, effect);
            buf.writeByte(amplifier);
            buf.writeVarInt(Math.min(MAX_EFFECT_TICKS, durationTicks));
            buf.writeByte(flags);
        }

        public static UpdateMobEffect read(FriendlyByteBuf buf)
        {
            int entityId = buf.readVarInt();
            MobEffect effect = buf.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
            byte amplifier = buf.readByte();
            int durationTicks = buf.readVarInt();
            byte flags = buf.readByte();
            return new UpdateMobEffect(entityId, effect, amplifier, durationTicks, flags);
        }

        public boolean handle(Supplier<NetworkEvent.Context> contextSupplier)
        {
            var level = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).orElseThrow();
            if (effect != null && level.getEntity(entityId) instanceof LivingEntity entity)
            {
                MobEffectInstance effectInstance = new MobEffectInstance(
                        effect, durationTicks, amplifier,
                        (flags & FLAG_AMBIENT) != 0, (flags & FLAG_VISIBLE) != 0, (flags & FLAG_SHOW_ICON) != 0
                );
                effectInstance.setNoCounter(durationTicks == MAX_EFFECT_TICKS);
                entity.forceAddEffect(effectInstance, null);
            }
            return true;
        }
    }

    public record RemoveMobEffect(int entityId, MobEffect effect)
    {

        public static RemoveMobEffect fromVanillaPacket(ClientboundRemoveMobEffectPacket vanillaPacket)
        {
            return new RemoveMobEffect(vanillaPacket.entityId, vanillaPacket.getEffect());
        }

        public void write(FriendlyByteBuf buf)
        {
            buf.writeVarInt(entityId);
            buf.writeRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS, effect);
        }

        public static RemoveMobEffect read(FriendlyByteBuf buf)
        {
            int entityId = buf.readVarInt();
            MobEffect effect = buf.readRegistryIdUnsafe(ForgeRegistries.MOB_EFFECTS);
            return new RemoveMobEffect(entityId, effect);
        }

        public boolean handle(Supplier<NetworkEvent.Context> contextSupplier)
        {
            var level = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT).orElseThrow();
            if (effect != null && level.getEntity(entityId) instanceof LivingEntity entity)
            {
                entity.removeEffectNoUpdate(effect);
            }
            return true;
        }

    }

}
