package net.minecraft.src.flora;

import net.minecraft.src.*;

public class WoodWallEntity extends EntityLiving
{
    public WoodWallEntity(World world)
    {
        super(world);
        texture = "/floratex/woodwall.png";
        setSize(0.9F, 1.3F);
    }

    public int getMaxHealth()
    {
        return 20;
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound)
    {
        super.writeEntityToNBT(nbttagcompound);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound)
    {
        super.readEntityFromNBT(nbttagcompound);
    }

    public void onLivingUpdate()
    {
    }

    protected String getDeathSound()
    {
        return "mob.cowhurt";
    }
}
