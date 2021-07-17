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

package net.minecraftforge.fmllegacy.network;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.tags.TagContainer;
import net.minecraft.tags.StaticTags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeTagHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.fmllegacy.LogicalSidedProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fmllegacy.common.registry.IEntityAdditionalSpawnData;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FMLPlayMessages
{
    /**
     * Used to spawn a custom entity without the same restrictions as
     * {@link net.minecraft.network.play.server.SSpawnObjectPacket} or {@link net.minecraft.network.play.server.SSpawnMobPacket}
     *
     * To customize how your entity is created clientside (instead of using the default factory provided to the {@link EntityType})
     * see {@link EntityType.Builder#setCustomClientFactory}.
     */
    public static class SpawnEntity
    {
        private final Entity entity;
        private final int typeId;
        private final int entityId;
        private final UUID uuid;
        private final double posX, posY, posZ;
        private final byte pitch, yaw, headYaw;
        private final int velX, velY, velZ;
        private final FriendlyByteBuf buf;

        SpawnEntity(Entity e)
        {
            this.entity = e;
            this.typeId = Registry.ENTITY_TYPE.getId(e.getType()); //TODO: Codecs
            this.entityId = e.getId();
            this.uuid = e.getUUID();
            this.posX = e.getX();
            this.posY = e.getY();
            this.posZ = e.getZ();
            this.pitch = (byte) Mth.floor(e.getXRot() * 256.0F / 360.0F);
            this.yaw = (byte) Mth.floor(e.getYRot() * 256.0F / 360.0F);
            this.headYaw = (byte) (e.getYHeadRot() * 256.0F / 360.0F);
            Vec3 vec3d = e.getDeltaMovement();
            double d1 = Mth.clamp(vec3d.x, -3.9D, 3.9D);
            double d2 = Mth.clamp(vec3d.y, -3.9D, 3.9D);
            double d3 = Mth.clamp(vec3d.z, -3.9D, 3.9D);
            this.velX = (int)(d1 * 8000.0D);
            this.velY = (int)(d2 * 8000.0D);
            this.velZ = (int)(d3 * 8000.0D);
            this.buf = null;
        }

        private SpawnEntity(int typeId, int entityId, UUID uuid, double posX, double posY, double posZ,
                byte pitch, byte yaw, byte headYaw, int velX, int velY, int velZ, FriendlyByteBuf buf)
        {
            this.entity = null;
            this.typeId = typeId;
            this.entityId = entityId;
            this.uuid = uuid;
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.pitch = pitch;
            this.yaw = yaw;
            this.headYaw = headYaw;
            this.velX = velX;
            this.velY = velY;
            this.velZ = velZ;
            this.buf = buf;
        }

        public static void encode(SpawnEntity msg, FriendlyByteBuf buf)
        {
            buf.writeVarInt(msg.typeId);
            buf.writeInt(msg.entityId);
            buf.writeLong(msg.uuid.getMostSignificantBits());
            buf.writeLong(msg.uuid.getLeastSignificantBits());
            buf.writeDouble(msg.posX);
            buf.writeDouble(msg.posY);
            buf.writeDouble(msg.posZ);
            buf.writeByte(msg.pitch);
            buf.writeByte(msg.yaw);
            buf.writeByte(msg.headYaw);
            buf.writeShort(msg.velX);
            buf.writeShort(msg.velY);
            buf.writeShort(msg.velZ);
            if (msg.entity instanceof IEntityAdditionalSpawnData)
            {
                ((IEntityAdditionalSpawnData) msg.entity).writeSpawnData(buf);
            }
        }

        public static SpawnEntity decode(FriendlyByteBuf buf)
        {
            return new SpawnEntity(
                    buf.readVarInt(),
                    buf.readInt(),
                    new UUID(buf.readLong(), buf.readLong()),
                    buf.readDouble(), buf.readDouble(), buf.readDouble(),
                    buf.readByte(), buf.readByte(), buf.readByte(),
                    buf.readShort(), buf.readShort(), buf.readShort(),
                    buf
                    );
        }

        public static void handle(SpawnEntity msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
                EntityType<?> type = Registry.ENTITY_TYPE.byId(msg.typeId);
                if (type == null)
                {
                    throw new RuntimeException(String.format("Could not spawn entity (id %d) with unknown type at (%f, %f, %f)", msg.entityId, msg.posX, msg.posY, msg.posZ));
                }

                Optional<Level> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
                Entity e = world.map(w->type.customClientSpawn(msg, w)).orElse(null);
                if (e == null)
                {
                    return;
                }

                e.setPacketCoordinates(msg.posX, msg.posY, msg.posZ);
                e.absMoveTo(msg.posX, msg.posY, msg.posZ, (msg.yaw * 360) / 256.0F, (msg.pitch * 360) / 256.0F);
                e.setYHeadRot((msg.headYaw * 360) / 256.0F);
                e.setYBodyRot((msg.headYaw * 360) / 256.0F);

                e.setId(msg.entityId);
                e.setUUID(msg.uuid);
                world.filter(ClientLevel.class::isInstance).ifPresent(w->((ClientLevel)w).putNonPlayerEntity(msg.entityId, e));
                e.lerpMotion(msg.velX / 8000.0, msg.velY / 8000.0, msg.velZ / 8000.0);
                if (e instanceof IEntityAdditionalSpawnData)
                {
                    ((IEntityAdditionalSpawnData) e).readSpawnData(msg.buf);
                }
            });
            ctx.get().setPacketHandled(true);
        }

        public Entity getEntity()
        {
            return entity;
        }

        public int getTypeId()
        {
            return typeId;
        }

        public int getEntityId()
        {
            return entityId;
        }

        public UUID getUuid()
        {
            return uuid;
        }

        public double getPosX()
        {
            return posX;
        }

        public double getPosY()
        {
            return posY;
        }

        public double getPosZ()
        {
            return posZ;
        }

        public byte getPitch()
        {
            return pitch;
        }

        public byte getYaw()
        {
            return yaw;
        }

        public byte getHeadYaw()
        {
            return headYaw;
        }

        public int getVelX()
        {
            return velX;
        }

        public int getVelY()
        {
            return velY;
        }

        public int getVelZ()
        {
            return velZ;
        }

        public FriendlyByteBuf getAdditionalData()
        {
            return buf;
        }
    }

    public static class OpenContainer
    {
        private final int id;
        private final int windowId;
        private final Component name;
        private final FriendlyByteBuf additionalData;

        OpenContainer(MenuType<?> id, int windowId, Component name, FriendlyByteBuf additionalData)
        {
            this(Registry.MENU.getId(id), windowId, name, additionalData);
        }

        private OpenContainer(int id, int windowId, Component name, FriendlyByteBuf additionalData)
        {
            this.id = id;
            this.windowId = windowId;
            this.name = name;
            this.additionalData = additionalData;
        }

        public static void encode(OpenContainer msg, FriendlyByteBuf buf)
        {
            buf.writeVarInt(msg.id);
            buf.writeVarInt(msg.windowId);
            buf.writeComponent(msg.name);
            buf.writeByteArray(msg.additionalData.readByteArray());
        }

        public static OpenContainer decode(FriendlyByteBuf buf)
        {
            return new OpenContainer(buf.readVarInt(), buf.readVarInt(), buf.readComponent(), new FriendlyByteBuf(Unpooled.wrappedBuffer(buf.readByteArray(32600))));
        }

        public static void handle(OpenContainer msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
                MenuScreens.getScreenFactory(msg.getType(), Minecraft.getInstance(), msg.getWindowId(), msg.getName())
                             .ifPresent(f -> {
                                 AbstractContainerMenu c = msg.getType().create(msg.getWindowId(), Minecraft.getInstance().player.getInventory(), msg.getAdditionalData());
                                 @SuppressWarnings("unchecked")
                                 Screen s = ((MenuScreens.ScreenConstructor<AbstractContainerMenu, ?>)f).create(c, Minecraft.getInstance().player.getInventory(), msg.getName());
                                 Minecraft.getInstance().player.containerMenu = ((MenuAccess<?>)s).getMenu();
                                 Minecraft.getInstance().setScreen(s);
                             });
            });
            ctx.get().setPacketHandled(true);
        }

        public final MenuType<?> getType() {
            return Registry.MENU.byId(this.id);
        }

        public int getWindowId() {
            return windowId;
        }

        public Component getName() {
            return name;
        }

        public FriendlyByteBuf getAdditionalData() {
            return additionalData;
        }
    }

    public static class SyncCustomTagTypes
    {
        private static final Logger LOGGER = LogManager.getLogger();
        private final Map<ResourceLocation, TagCollection<?>> customTagTypeCollections;

        SyncCustomTagTypes(Map<ResourceLocation, TagCollection<?>> customTagTypeCollections)
        {
            this.customTagTypeCollections = customTagTypeCollections;
        }

        public Map<ResourceLocation, TagCollection<?>> getCustomTagTypes()
        {
            return customTagTypeCollections;
        }

        public static void encode(SyncCustomTagTypes msg, FriendlyByteBuf buf)
        {
            buf.writeVarInt(msg.customTagTypeCollections.size());
            msg.customTagTypeCollections.forEach((registryName, modded) -> forgeTagCollectionWrite(buf, registryName, modded.getAllTags()));
        }

        private static <T> void forgeTagCollectionWrite(FriendlyByteBuf buf, ResourceLocation registryName, Map<ResourceLocation, Tag<T>> tags)
        {
            buf.writeResourceLocation(registryName);
            buf.writeVarInt(tags.size());
            tags.forEach((name, tag) -> {
                buf.writeResourceLocation(name);
                List<T> elements = tag.getValues();
                buf.writeVarInt(elements.size());
                for (T element : elements)
                {
                    buf.writeResourceLocation(((IForgeRegistryEntry<?>) element).getRegistryName());
                }
            });
        }

        public static SyncCustomTagTypes decode(FriendlyByteBuf buf)
        {
            ImmutableMap.Builder<ResourceLocation, TagCollection<?>> builder = ImmutableMap.builder();
            int size = buf.readVarInt();
            for (int i = 0; i < size; i++)
            {
                ResourceLocation regName = buf.readResourceLocation();
                IForgeRegistry<?> registry = RegistryManager.ACTIVE.getRegistry(regName);
                if (registry != null)
                {
                    builder.put(regName, readTagCollection(buf, registry));
                }
            }
            return new SyncCustomTagTypes(builder.build());
        }

        private static <T extends IForgeRegistryEntry<T>> TagCollection<T> readTagCollection(FriendlyByteBuf buf, IForgeRegistry<T> registry)
        {
            Map<ResourceLocation, Tag<T>> tags = Maps.newHashMap();
            int totalTags = buf.readVarInt();
            for (int i = 0; i < totalTags; i++)
            {
                ImmutableSet.Builder<T> elementBuilder = ImmutableSet.builder();
                ResourceLocation name = buf.readResourceLocation();
                int totalElements = buf.readVarInt();
                for (int j = 0; j < totalElements; j++)
                {
                    T element = registry.getValue(buf.readResourceLocation());
                    if (element != null)
                    {
                        elementBuilder.add(element);
                    }
                }
                tags.put(name, Tag.fromSet(elementBuilder.build()));
            }
            return TagCollection.of(tags);
        }

        public static void handle(SyncCustomTagTypes msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
                if (Minecraft.getInstance().level != null)
                {
                    TagContainer tagCollectionSupplier = Minecraft.getInstance().level.getTagManager();
                    //Validate that all the tags exist using the tag type collections from the packet
                    // We mimic vanilla in that we validate before updating the actual stored tags so that it can gracefully fallback
                    // to the last working set of tags
                    //Note: We gracefully ignore any tag types the server may have that we don't as they won't be in our tag registry
                    // so they won't be validated
                    //Override and use the tags from the packet to test for validation before we actually set them
                    Multimap<ResourceKey<? extends Registry<?>>, ResourceLocation> missingTags = StaticTags.getAllMissingTags(ForgeTagHandler.withSpecificCustom(tagCollectionSupplier, msg.customTagTypeCollections));
                    if (missingTags.isEmpty())
                    {
                        //If we have no missing tags, update the custom tag types
                        ForgeTagHandler.updateCustomTagTypes(msg);
                        if (!ctx.get().getNetworkManager().isMemoryConnection())
                        {
                            //And if everything hasn't already been set due to being in single player
                            // Fetch and update the custom tag types. We skip vanilla tag types as they have already been fetched
                            // And fire an event that the custom tag types have been updated
                            StaticTags.fetchCustomTagTypes(tagCollectionSupplier);
                            MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent.CustomTagTypes(tagCollectionSupplier));
                        }
                    } else {
                        LOGGER.warn("Incomplete server tags, disconnecting. Missing: {}", missingTags);
                        ctx.get().getNetworkManager().disconnect(new TranslatableComponent("multiplayer.disconnect.missing_tags"));
                    }
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
