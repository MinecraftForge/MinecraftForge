package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityLiving;
import net.minecraft.src.DataWatcher;
import net.minecraft.src.MathHelper;
import net.minecraft.src.forge.ISpawnHandler;
import net.minecraft.src.forge.IThrowableEntity;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;

public class PacketEntitySpawn extends ForgePacket
{
    public int modID;
    public int entityID;
    public int typeID;
    public int posX;
    public int posY;
    public int posZ;
    public byte yaw;
    public byte pitch;
    public byte yawHead;
    public int throwerID;
    public int speedX;
    public int speedY;
    public int speedZ;
    public Object metadata;
    private ISpawnHandler handler;

    public PacketEntitySpawn(){}
    public PacketEntitySpawn(Entity ent, NetworkMod mod, int type)
    {
        entityID = ent.entityId;

        posX = MathHelper.floor_double(ent.posX * 32D);
        posY = MathHelper.floor_double(ent.posY * 32D);
        posZ = MathHelper.floor_double(ent.posZ * 32D);

        typeID = type;
        modID = MinecraftForge.getModID(mod);

        yaw      = (byte)(ent.rotationYaw * 256.0F / 360.0F);
        pitch    = (byte)(ent.rotationPitch * 256.0F / 360.0F);
        yawHead  = (byte)(ent instanceof EntityLiving ? ((EntityLiving)ent).rotationYawHead * 256.0F / 360.0F : 0);
        metadata = ent.getDataWatcher();

        if (ent instanceof IThrowableEntity)
        {
            Entity owner = ((IThrowableEntity)ent).getThrower();
            throwerID = (owner == null ? ent.entityId : owner.entityId);
            double maxVel = 3.9D;
            double mX = ent.motionX;
            double mY = ent.motionY;
            double mZ = ent.motionZ;
            if (mX < -maxVel) mX = -maxVel;
            if (mY < -maxVel) mY = -maxVel;
            if (mZ < -maxVel) mZ = -maxVel;
            if (mX >  maxVel) mX =  maxVel;
            if (mY >  maxVel) mY =  maxVel;
            if (mZ >  maxVel) mZ =  maxVel;
            speedX = (int)(mX * 8000D);
            speedY = (int)(mY * 8000D);
            speedZ = (int)(mZ * 8000D);
        }
        if (ent instanceof ISpawnHandler)
        {
            handler = (ISpawnHandler)ent;
        }
    }
    public void writeData(DataOutputStream data) throws IOException
    {
        data.writeInt(modID);
        data.writeInt(entityID);
        data.writeByte(typeID & 0xFF);
        data.writeInt(posX);
        data.writeInt(posY);
        data.writeInt(posZ);
        data.writeByte(yaw);
        data.writeByte(pitch);
        data.writeByte(yawHead);
        ((DataWatcher)metadata).writeWatchableObjects(data);
        data.writeInt(throwerID);
        if (throwerID != 0)
        {
            data.writeShort(speedX);
            data.writeShort(speedY);
            data.writeShort(speedZ);
        }
        if (handler != null)
        {
            handler.writeSpawnData(data);
        }
    }

    public void readData(DataInputStream data) throws IOException
    {
        modID     = data.readInt();
        entityID  = data.readInt();
        typeID    = data.readByte() & 0xFF;
        posX      = data.readInt();
        posY      = data.readInt();
        posZ      = data.readInt();
        yaw       = data.readByte();
        pitch     = data.readByte();
        yawHead   = data.readByte();
        metadata  = DataWatcher.readWatchableObjects(data);
        throwerID = data.readInt();
        if (throwerID != 0)
        {
            speedX = data.readShort();
            speedY = data.readShort();
            speedZ = data.readShort();
        }
    }
    @Override
    public int getID()
    {
        return ForgePacket.SPAWN;
    }
}
