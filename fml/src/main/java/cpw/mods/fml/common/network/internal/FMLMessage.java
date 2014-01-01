package cpw.mods.fml.common.network.internal;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.List;

import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.registry.EntityRegistry.EntityRegistration;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import cpw.mods.fml.common.registry.IThrowableEntity;
import cpw.mods.fml.relauncher.Side;

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
            buf.writeInt(entity.func_145782_y());
        }

        @Override
        void fromBytes(ByteBuf buf)
        {
            entityId = buf.readInt();
        }
    }

    public static class EntityAdjustMessage extends EntityMessage {
        int serverX;
        int serverY;
        int serverZ;

        public EntityAdjustMessage() {}
        public EntityAdjustMessage(Entity entity, int serverX, int serverY, int serverZ)
        {
            super(entity);
            this.serverX = serverX;
            this.serverY = serverY;
            this.serverZ = serverZ;
        }

        @Override
        void toBytes(ByteBuf buf)
        {
            super.toBytes(buf);
            buf.writeInt(serverX);
            buf.writeInt(serverY);
            buf.writeInt(serverZ);
        }

        @Override
        void fromBytes(ByteBuf buf)
        {
            super.fromBytes(buf);
            serverX = buf.readInt();
            serverY = buf.readInt();
            serverZ = buf.readInt();
        }
    }
    public static class EntitySpawnMessage extends EntityMessage {
        String modId;
        int modEntityTypeId;
        int rawX;
        int rawY;
        int rawZ;
        double scaledX;
        double scaledY;
        double scaledZ;
        float scaledYaw;
        float scaledPitch;
        float scaledHeadYaw;
        int throwerId;
        double speedScaledX;
        double speedScaledY;
        double speedScaledZ;
        @SuppressWarnings("rawtypes")
        List dataWatcherList;
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
            // posX, posY, posZ
            buf.writeInt(MathHelper.func_76128_c(entity.field_70165_t * 32D));
            buf.writeInt(MathHelper.func_76128_c(entity.field_70163_u * 32D));
            buf.writeInt(MathHelper.func_76128_c(entity.field_70161_v * 32D));
            // yaw, pitch
            buf.writeByte((byte)(entity.field_70177_z * 256.0F / 360.0F));
            buf.writeByte((byte) (entity.field_70125_A * 256.0F / 360.0F));
            // head yaw
            if (entity instanceof EntityLivingBase)
            {
                buf.writeByte((byte) (((EntityLivingBase)entity).field_70759_as * 256.0F / 360.0F));
            }

            ByteBuf tmpBuf = Unpooled.buffer();
            PacketBuffer pb = new PacketBuffer(tmpBuf);
            try
            {
                entity.func_70096_w().func_151509_a(pb);
            } catch (IOException e)
            {
                // Sigh
            }
            buf.writeBytes(tmpBuf);

            if (entity instanceof IThrowableEntity)
            {
                Entity owner = ((IThrowableEntity)entity).getThrower();
                buf.writeInt(owner == null ? entity.func_145782_y() : owner.func_145782_y());
                double maxVel = 3.9D;
                double mX = entity.field_70159_w;
                double mY = entity.field_70181_x;
                double mZ = entity.field_70179_y;
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
            rawX = dat.readInt();
            rawY = dat.readInt();
            rawZ = dat.readInt();
            scaledX = rawX / 32D;
            scaledY = rawY / 32D;
            scaledZ = rawZ / 32D;
            scaledYaw = dat.readByte() * 360F / 256F;
            scaledPitch = dat.readByte() * 360F / 256F;
            scaledHeadYaw = dat.readByte() * 360F / 256F;
            try
            {
                dataWatcherList = DataWatcher.func_151508_b(new PacketBuffer(dat));
            } catch (IOException e)
            {
                // Sigh
            }

            throwerId = dat.readInt();
            if (throwerId != 0)
            {
                speedScaledX = dat.readInt() / 8000D;
                speedScaledY = dat.readInt() / 8000D;
                speedScaledZ = dat.readInt() / 8000D;
            }
            this.dataStream = dat;
        }
    }
    abstract void toBytes(ByteBuf buf);
    abstract void fromBytes(ByteBuf buf);
}
