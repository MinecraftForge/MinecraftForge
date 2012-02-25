package net.minecraft.src.forge.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.BaseMod;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;
import net.minecraft.src.forge.ISpawnHandler;
import net.minecraft.src.forge.IThrowableEntity;
import net.minecraft.src.forge.MinecraftForge;
import net.minecraft.src.forge.NetworkMod;

public class PacketEntitySpawn extends ForgePacket
{
    public int entityID;
    public int posX;
    public int posY;
    public int posZ;
    public int speedX;
    public int speedY;
    public int speedZ;
    public int typeID;
    public int modID;
    public int throwerID;
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
        data.writeByte(typeID);
        data.writeInt(posX);
        data.writeInt(posY);
        data.writeInt(posZ);
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
        typeID    = data.readByte();
        posX      = data.readInt();
        posY      = data.readInt();
        posZ      = data.readInt();
        throwerID = data.readInt();
        if (throwerID != 0)
        {
            speedX = data.readShort();
            speedY = data.readShort();
            speedX = data.readShort();
        }
    }
    @Override
    public int getID()
    {
        return ForgePacket.SPAWN;
    }
}
