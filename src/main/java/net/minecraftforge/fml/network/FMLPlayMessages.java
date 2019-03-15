/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.ModList;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class FMLPlayMessages
{
    public static class SpawnEntity
    {
        private final Entity entity;
        private final int typeId;
        private final int entityId;
        private final UUID uuid;
        private final double posX, posY, posZ;
        private final byte pitch, yaw, headYaw;
        private final short velX, velY, velZ;
        private final PacketBuffer buf;

        SpawnEntity(Entity e)
        {
            this.entity = e;
            this.typeId = IRegistry.field_212629_r.getId(e.getType());
            this.entityId = e.getEntityId();
            this.uuid = e.getUniqueID();
            this.posX = e.posX;
            this.posY = e.posY;
            this.posZ = e.posZ;
            this.pitch = (byte) MathHelper.floor(e.rotationPitch * 256.0F / 360.0F);
            this.yaw = (byte) MathHelper.floor(e.rotationYaw * 256.0F / 360.0F);
            this.headYaw = (byte) (e.getRotationYawHead() * 256.0F / 360.0F);
            this.velX = (short)(MathHelper.clamp(e.motionX, -3.9D, 3.9D) * 8000.0D);
            this.velY = (short)(MathHelper.clamp(e.motionY, -3.9D, 3.9D) * 8000.0D);
            this.velZ = (short)(MathHelper.clamp(e.motionZ, -3.9D, 3.9D) * 8000.0D);
            this.buf = null;
        }

        private SpawnEntity(int typeId, int entityId, UUID uuid, double posX, double posY, double posZ,
                           byte pitch, byte yaw, byte headYaw, short velX, short velY, short velZ, PacketBuffer buf)
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
                EntityType<?> type = IRegistry.field_212629_r.get(msg.typeId);
                if (type == null)
                {
                    throw new RuntimeException(String.format("Could not spawn entity (id %d) with unknown type at (%f, %f, %f)", msg.entityId, msg.posX, msg.posY, msg.posZ));
                }

                Optional<World> world = LogicalSidedProvider.CLIENTWORLD.get(ctx.get().getDirection().getReceptionSide());
                Entity e = world.map(w->type.handleSpawnMessage(w, msg)).orElse(null);
                if (e == null)
                {
                    return;
                }

                EntityTracker.updateServerPosition(e, msg.posX, msg.posY, msg.posZ);
                e.setPositionAndRotation(msg.posX, msg.posY, msg.posZ, (msg.yaw * 360) / 256.0F, (msg.pitch * 360) / 256.0F);
                e.setRotationYawHead((msg.headYaw * 360) / 256.0F);
                e.setRenderYawOffset((msg.headYaw * 360) / 256.0F);

                Entity[] parts = e.getParts();
                if (parts != null)
                {
                    int offset = msg.entityId - e.getEntityId();
                    for (Entity part : parts)
                    {
                        part.setEntityId(part.getEntityId() + offset);
                    }
                }

                e.setEntityId(msg.entityId);
                e.setUniqueId(msg.uuid);
                Minecraft.getInstance().world.addEntityToWorld(msg.entityId, e);
                e.setVelocity(msg.velX / 8000.0, msg.velY / 8000.0, msg.velZ / 8000.0);
                if (e instanceof IEntityAdditionalSpawnData)
                {
                    ((IEntityAdditionalSpawnData) e).readSpawnData(msg.buf);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }

    public static class OpenContainer
    {
        private final ResourceLocation id;
        private final int windowId;
        private final PacketBuffer additionalData;

        OpenContainer(ResourceLocation id, int windowId, PacketBuffer additionalData)
        {
            this.id = id;
            this.windowId = windowId;
            this.additionalData = additionalData;
        }

        public static void encode(OpenContainer msg, PacketBuffer buf)
        {
            buf.writeResourceLocation(msg.id);
            buf.writeVarInt(msg.windowId);
            buf.writeByteArray(msg.additionalData.readByteArray());
        }

        public static OpenContainer decode(PacketBuffer buf)
        {
            return new OpenContainer(buf.readResourceLocation(), buf.readVarInt(), new PacketBuffer(Unpooled.wrappedBuffer(buf.readByteArray(32600))));
        }

        public static void handle(OpenContainer msg, Supplier<NetworkEvent.Context> ctx)
        {
            ctx.get().enqueueWork(() -> ModList.get().getModContainerById(msg.id.getNamespace()).ifPresent(mc->
            mc.getCustomExtension(ExtensionPoint.GUIFACTORY).map(f -> f.apply(msg)).ifPresent(gui-> {
                    Minecraft.getInstance().displayGuiScreen(gui);
                    Minecraft.getInstance().player.openContainer.windowId = msg.windowId;
            })));
            ctx.get().setPacketHandled(true);
        }

        public final ResourceLocation getId() {
            return this.id;
        }

        public int getWindowId() {
            return windowId;
        }

        public PacketBuffer getAdditionalData() {
            return additionalData;
        }
    }
}
