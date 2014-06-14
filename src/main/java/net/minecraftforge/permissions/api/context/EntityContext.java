package net.minecraftforge.permissions.api.context;

import java.util.UUID;

import net.minecraft.entity.Entity;

public class EntityContext implements IContext
{
    private final double x, y, z;
    private final int dim;
    private final float pitch, yaw;
    private final UUID entityId;

    public EntityContext(Entity entity)
    {
        dim = entity.worldObj.provider.dimensionId;
        entityId = entity.getPersistentID();
        x = entity.posX;
        y = entity.posY;
        z = entity.posZ;
        pitch = entity.rotationPitch;
        yaw = entity.rotationYaw;
    }
    

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getZ()
    {
        return z;
    }

    public int getDimensionId()
    {
        return dim;
    }

    public UUID getEntityId()
    {
        return entityId;
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }
}
