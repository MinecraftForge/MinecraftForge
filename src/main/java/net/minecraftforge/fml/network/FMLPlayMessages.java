/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryManager;

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
        private final PacketBuffer buf;

        SpawnEntity(Entity e)
        {
            this.entity = e;
            this.typeId = Registry.ENTITY_TYPE.getId(e.getType());
            this.entityId = e.getEntityId();
            this.uuid = e.getUniqueID();
            this.posX = e.getPosX();
            this.posY = e.getPosY();
            this.posZ = e.getPosZ();
            this.pitch = (byte) MathHelper.floor(e.rotationPitch * 256.0F / 360.0F);
            this.yaw = (byte) MathHelper.floor(e.rotationYaw * 256.0F / 360.0F);
            this.headYaw = (byte) (e.getRotationYawHead() * 256.0F / 360.0F);
            Vector3d vec3d = e.getMotion();
            double d1 = MathHelper.clamp(vec3d.x, -3.9D, 3.9D);
            double d2 = MathHelper.clamp(vec3d.y, -3.9D, 3.9D);
            double d3 = MathHelper.clamp(vec3d.z, -3.9D, 3.9D);
            this.velX = (int)(d1 * 8000.0D);
            this.velY = (int)(d2 * 8000.0D);
            this.velZ = (int)(d3 * 8000.0D);
            this.buf = null;
        }

        private SpawnEntity(int typeId, int entityId, UUID uuid, double posX, double posY, double posZ,
                byte pitch, byte yaw, byte headYaw, int velX, int velY, int velZ, PacketBuffer buf)
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

        public static void encode(SpawnEntity msg, PacketBuffer buf)
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

        public static SpawnEntity decode(PacketBuffer buf)
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
                EntityType<?> type = Registry.ENTITY_TYPE.getByValue(msg.typeId);
                if (type == null)
                {
                    throw new RuntimeException(String.format("Could not spawn entity (id %d) with unknown type at (%f, %f, %f)", msg.entityId, msg.posX, msg.posY, msg.posZ));
                }

                Optional<World> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
                Entity e = world.map(w->type.customClientSpawn(msg, w)).orElse(null);
                if (e == null)
                {
                    return;
                }

                e.setPacketCoordinates(msg.posX, msg.posY, msg.posZ);
                e.setPositionAndRotation(msg.posX, msg.posY, msg.posZ, (msg.yaw * 360) / 256.0F, (msg.pitch * 360) / 256.0F);
                e.setRotationYawHead((msg.headYaw * 360) / 256.0F);
                e.setRenderYawOffset((msg.headYaw * 360) / 256.0F);

                e.setEntityId(msg.entityId);
                e.setUniqueId(msg.uuid);
                world.filter(ClientWorld.class::isInstance).ifPresent(w->((ClientWorld)w).addEntity(msg.entityId, e));
                e.setVelocity(msg.velX / 8000.0, msg.velY / 8000.0, msg.velZ / 8000.0);
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

        public PacketBuffer getAdditionalData()
        {
            return buf;
        }
    }

    public static class OpenContainer
    {
        private final int id;
        private final int windowId;
        private final ITextComponent name;
        private final PacketBuffer additionalData;

        OpenContainer(ContainerType<?> id, int windowId, ITextComponent name, PacketBuffer additionalData)
        {
            this(Registry.MENU.getId(id), windowId, name, additionalData);
        }

        private OpenContainer(int id, int windowId, ITextComponent name, PacketBuffer additionalData)
        {
            this.id = id;
            this.windowId = windowId;
            this.name = name;
            this.additionalData = additionalData;
        }

        public static void encode(OpenContainer msg, PacketBuffer buf)
        {
            buf.writeVarInt(msg.id);
            buf.writeVarInt(msg.windowId);
            buf.writeTextComponent(msg.name);
            buf.writeByteArray(msg.additionalData.readByteArray());
        }

        public static OpenContainer decode(PacketBuffer buf)
        {
            return new OpenContainer(buf.readVarInt(), buf.readVarInt(), buf.readTextComponent(), new PacketBuffer(Unpooled.wrappedBuffer(buf.readByteArray(32600))));
        }

        public static void handle(OpenContainer msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
                ScreenManager.getScreenFactory(msg.getType(), Minecraft.getInstance(), msg.getWindowId(), msg.getName())
                             .ifPresent(f -> {
                                 Container c = msg.getType().create(msg.getWindowId(), Minecraft.getInstance().player.inventory, msg.getAdditionalData());
                                 @SuppressWarnings("unchecked")
                                 Screen s = ((ScreenManager.IScreenFactory<Container, ?>)f).create(c, Minecraft.getInstance().player.inventory, msg.getName());
                                 Minecraft.getInstance().player.openContainer = ((IHasContainer<?>)s).getContainer();
                                 Minecraft.getInstance().displayGuiScreen(s);
                             });
            });
            ctx.get().setPacketHandled(true);
        }

        public final ContainerType<?> getType() {
            return Registry.MENU.getByValue(this.id);
        }

        public int getWindowId() {
            return windowId;
        }

        public ITextComponent getName() {
            return name;
        }

        public PacketBuffer getAdditionalData() {
            return additionalData;
        }
    }

    public static class SyncCustomTagTypes
    {
        private final Map<ResourceLocation, ITagCollection<?>> moddedTagCollections;

        SyncCustomTagTypes(Map<ResourceLocation, ITagCollection<?>> moddedTagCollections)
        {
            this.moddedTagCollections = moddedTagCollections;
        }

        public static void encode(SyncCustomTagTypes msg, PacketBuffer buf)
        {
            buf.writeVarInt(msg.moddedTagCollections.size());
            msg.moddedTagCollections.forEach((registryName, modded) -> forgeTagCollectionWrite(buf, registryName, modded.func_241833_a()));
        }

        private static <T> void forgeTagCollectionWrite(PacketBuffer buf, ResourceLocation registryName, Map<ResourceLocation, ITag<T>> tags)
        {
            buf.writeResourceLocation(registryName);
            buf.writeVarInt(tags.size());
            tags.forEach((name, tag) -> {
                buf.writeResourceLocation(name);
                List<T> elements = tag.func_230236_b_();
                buf.writeVarInt(elements.size());
                for (T element : elements)
                {
                    //TODO: Make this not need to be unchecked
                    buf.writeResourceLocation(((IForgeRegistryEntry<?>) element).getRegistryName());
                }
            });
        }

        public static SyncCustomTagTypes decode(PacketBuffer buf)
        {
            ImmutableMap.Builder<ResourceLocation, ITagCollection<?>> builder = ImmutableMap.builder();
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

        private static <T extends IForgeRegistryEntry<T>> ITagCollection<T> readTagCollection(PacketBuffer buf, IForgeRegistry<T> registry)
        {
            Map<ResourceLocation, ITag<T>> tags = Maps.newHashMap();
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
                tags.put(name, ITag.func_232946_a_(elementBuilder.build()));
            }
            return ITagCollection.func_242202_a(tags);
        }

        public static void handle(SyncCustomTagTypes msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> {
                //TODO: Create any missing types on the client
                //TODO: Replace existing supplier with one that has our existing types or make it possible to modify to add our custom types
                //TODO: Also figure out how we want to handle the tags updated event being fired
                /*ITagCollectionSupplier tagCollectionSupplier;
                Multimap<ResourceLocation, ResourceLocation> multimap = TagRegistryManager.func_242198_b(tagCollectionSupplier);
                if (!multimap.isEmpty()) {
                    LOGGER.warn("Incomplete server tags, disconnecting. Missing: {}", multimap);
                    ctx.get().getNetworkManager().closeChannel(new TranslationTextComponent("multiplayer.disconnect.missing_tags"));
                } else {
                    this.networkTagManager = tagCollectionSupplier;
                    if (!ctx.get().getNetworkManager().isLocalChannel()) {
                        //tagCollectionSupplier.func_242212_e();
                        //TODO: Evaluate
                        TagRegistryManager.func_242193_a(tagCollectionSupplier);
                        MinecraftForge.EVENT_BUS.post(new TagsUpdatedEvent.CustomTagTypes(tagCollectionSupplier));
                    }
                }*/
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
