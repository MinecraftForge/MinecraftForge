package net.minecraft.src.forge;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.src.BaseMod;
import net.minecraft.src.Entity;
import net.minecraft.src.MathHelper;

public class PacketEntitySpawn 
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
    
    public PacketEntitySpawn(){}
    public PacketEntitySpawn(Entity ent, BaseMod mod, int type)
    {
        entityID = ent.entityId; 

        posX = MathHelper.floor_double(ent.posX * 32D);
        posY = MathHelper.floor_double(ent.posY * 32D);
        posZ = MathHelper.floor_double(ent.posZ * 32D);
        
        typeID = type;
        modID = mod.toString().hashCode();
        
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
}
