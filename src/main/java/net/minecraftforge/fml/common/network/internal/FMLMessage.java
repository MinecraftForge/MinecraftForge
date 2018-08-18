/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common.network.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.EntityRegistry.EntityRegistration;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;

public abstract class FMLMessage {
    public static class CompleteHandshake extends FMLMessage {
        Side target;
        public CompleteHandshake() {
        }
        public CompleteHandshake(Side target)
        {
            this.target = target;
        }
        @Override
        void fromBytes(ByteBuf buf)
        {
            target = Side.values()[buf.readByte()];
        }
        @Override
        void toBytes(ByteBuf buf)
        {
            buf.writeByte(target.ordinal());
        }
    }
    public static class OpenGui extends FMLMessage {
        int windowId;
        String modId;
        int modGuiId;
        int x;
        int y;
        int z;

        public OpenGui() {}
        OpenGui(int windowId, String modId, int modGuiId, int x, int y, int z)
        {
            this.windowId = windowId;
            this.modId = modId;
            this.modGuiId = modGuiId;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        void toBytes(ByteBuf buf)
        {
            buf.writeInt(windowId);
            ByteBufUtils.writeUTF8String(buf, modId);
            buf.writeInt(modGuiId);
            buf.writeInt(x);
            buf.writeInt(y);
            buf.writeInt(z);
        }

        @Override
        void fromBytes(ByteBuf buf)
        {
            windowId = buf.readInt();
            modId = ByteBufUtils.readUTF8String(buf);
            modGuiId = buf.readInt();
            x = buf.readInt();
            y = buf.readInt();
            z = buf.readInt();
        }
    }

    public abstract static class EntityMessage extends FMLMessage {
        Entity entity;
        int entityId;
        public EntityMessage()
        {

        }
        EntityMessage(Entity entity)
        {
            this.entity = entity;
        }
        @Override
        void toBytes(ByteBuf buf)
        {
            buf.writeInt(entity.getEntityId());
        }

        @Override
        void fromBytes(ByteBuf buf)
        {
            entityId = buf.readInt();
        }
    }

    public static class EntitySpawnMessage extends EntityMessage {
        String modId;
        int modEntityTypeId;
        UUID entityUUID;
        double rawX;
        double rawY;
        double rawZ;
        float scaledYaw;
        float scaledPitch;
        float scaledHeadYaw;
        int throwerId;
        double speedScaledX;
        double speedScaledY;
        double speedScaledZ;
        List<EntityDataManager.DataEntry<?>> dataWatcherList;
        ByteBuf dataStream;

        public EntitySpawnMessage() {}
        public EntitySpawnMessage(EntityRegistration er, Entity entity, ModContainer modContainer)
        {
            super(entity);
            modId = modContainer.getModId();
            modEntityTypeId = er.getModEntityId();
        }
        @Override
        void toBytes(ByteBuf buf)
        {
            super.toBytes(buf);
            ByteBufUtils.writeUTF8String(buf, modId);
            buf.writeInt(modEntityTypeId);
            buf.writeLong(entity.getUniqueID().getMostSignificantBits());
            buf.writeLong(entity.getUniqueID().getLeastSignificantBits());
            // posX, posY, posZ
            buf.writeDouble(entity.posX);
            buf.writeDouble(entity.posY);
            buf.writeDouble(entity.posZ);
            // yaw, pitch
            buf.writeByte((byte)(entity.rotationYaw * 256.0F / 360.0F));
            buf.writeByte((byte) (entity.rotationPitch * 256.0F / 360.0F));
            // head yaw
            if (entity instanceof EntityLivingBase)
            {
                buf.writeByte((byte) (((EntityLivingBase)entity).rotationYawHead * 256.0F / 360.0F));
            }
            else
            {
                buf.writeByte(0);
            }
            ByteBuf tmpBuf = Unpooled.buffer();
            PacketBuffer pb = new PacketBuffer(tmpBuf);
            try
            {
                entity.getDataManager().writeEntries(pb);
            }
            catch (IOException e)
            {
                FMLLog.log.fatal("Encountered fatal exception trying to send entity spawn data watchers", e);
                throw new RuntimeException(e);
            }
            buf.writeBytes(tmpBuf);

            if (entity instanceof IThrowableEntity)
            {
                Entity owner = ((IThrowableEntity)entity).getThrower();
                buf.writeInt(owner == null ? entity.getEntityId() : owner.getEntityId());
                double maxVel = 3.9D;
                double mX = entity.motionX;
                double mY = entity.motionY;
                double mZ = entity.motionZ;
                if (mX < -maxVel) mX = -maxVel;
                if (mY < -maxVel) mY = -maxVel;
                if (mZ < -maxVel) mZ = -maxVel;
                if (mX >  maxVel) mX =  maxVel;
                if (mY >  maxVel) mY =  maxVel;
                if (mZ >  maxVel) mZ =  maxVel;
                buf.writeInt((int)(mX * 8000D));
                buf.writeInt((int)(mY * 8000D));
                buf.writeInt((int)(mZ * 8000D));
            }
            else
            {
                buf.writeInt(0);
            }
            if (entity instanceof IEntityAdditionalSpawnData)
            {
                tmpBuf = Unpooled.buffer();
                ((IEntityAdditionalSpawnData)entity).writeSpawnData(tmpBuf);
                buf.writeBytes(tmpBuf);
            }
        }
        @Override
        void fromBytes(ByteBuf dat)
        {
            super.fromBytes(dat);
            modId = ByteBufUtils.readUTF8String(dat);
            modEntityTypeId = dat.readInt();
            entityUUID = new UUID(dat.readLong(), dat.readLong());
            rawX = dat.readDouble();
            rawY = dat.readDouble();
            rawZ = dat.readDouble();
            scaledYaw = dat.readByte() * 360F / 256F;
            scaledPitch = dat.readByte() * 360F / 256F;
            scaledHeadYaw = dat.readByte() * 360F / 256F;
            try
            {
                dataWatcherList = EntityDataManager.readEntries(new PacketBuffer(dat));
            }
            catch (IOException e)
            {
                FMLLog.log.fatal("There was a critical error decoding the datawatcher stream for a mod entity.", e);
                throw new RuntimeException(e);
            }

            throwerId = dat.readInt();
            if (throwerId != 0)
            {
                speedScaledX = dat.readInt() / 8000D;
                speedScaledY = dat.readInt() / 8000D;
                speedScaledZ = dat.readInt() / 8000D;
            }
            this.dataStream = dat.retain();
        }
    }
    abstract void toBytes(ByteBuf buf);
    abstract void fromBytes(ByteBuf buf);
}
