package net.minecraft.entity.monster;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.world.World;

public abstract class EntityGolem extends EntityCreature implements IAnimals
{
    private static final String __OBFID = "CL_00001644";

    public EntityGolem(World par1World)
    {
        super(par1World);
    }

    // JAVADOC METHOD $$ func_70069_a
    protected void fall(float par1) {}

    // JAVADOC METHOD $$ func_70639_aQ
    protected String getLivingSound()
    {
        return "none";
    }

    // JAVADOC METHOD $$ func_70621_aR
    protected String getHurtSound()
    {
        return "none";
    }

    // JAVADOC METHOD $$ func_70673_aS
    protected String getDeathSound()
    {
        return "none";
    }

    // JAVADOC METHOD $$ func_70627_aG
    public int getTalkInterval()
    {
        return 120;
    }

    // JAVADOC METHOD $$ func_70692_ba
    protected boolean canDespawn()
    {
        return false;
    }
}