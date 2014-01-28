package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class EntityAmbientCreature extends EntityLiving implements IAnimals
{
    private static final String __OBFID = "CL_00001636";

    public EntityAmbientCreature(World par1World)
    {
        super(par1World);
    }

    public boolean allowLeashing()
    {
        return false;
    }

    // JAVADOC METHOD $$ func_70085_c
    protected boolean interact(EntityPlayer par1EntityPlayer)
    {
        return false;
    }
}