package net.minecraft.entity.passive;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public abstract class EntityWaterMob extends EntityCreature implements IAnimals
{
    private static final String __OBFID = "CL_00001653";

    public EntityWaterMob(World par1World)
    {
        super(par1World);
    }

    public boolean canBreatheUnderwater()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_70601_bi
    public boolean getCanSpawnHere()
    {
        return this.worldObj.checkNoEntityCollision(this.boundingBox);
    }

    // JAVADOC METHOD $$ func_70627_aG
    public int getTalkInterval()
    {
        return 120;
    }

    // JAVADOC METHOD $$ func_70692_ba
    protected boolean canDespawn()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_70693_a
    protected int getExperiencePoints(EntityPlayer par1EntityPlayer)
    {
        return 1 + this.worldObj.rand.nextInt(3);
    }

    // JAVADOC METHOD $$ func_70030_z
    public void onEntityUpdate()
    {
        int i = this.getAir();
        super.onEntityUpdate();

        if (this.isEntityAlive() && !this.isInWater())
        {
            --i;
            this.setAir(i);

            if (this.getAir() == -20)
            {
                this.setAir(0);
                this.attackEntityFrom(DamageSource.drown, 2.0F);
            }
        }
        else
        {
            this.setAir(300);
        }
    }
}