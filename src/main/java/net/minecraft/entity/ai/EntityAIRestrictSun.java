package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;

public class EntityAIRestrictSun extends EntityAIBase
{
    private EntityCreature theEntity;
    private static final String __OBFID = "CL_00001611";

    public EntityAIRestrictSun(EntityCreature par1EntityCreature)
    {
        this.theEntity = par1EntityCreature;
    }

    // JAVADOC METHOD $$ func_75250_a
    public boolean shouldExecute()
    {
        return this.theEntity.worldObj.isDaytime();
    }

    // JAVADOC METHOD $$ func_75249_e
    public void startExecuting()
    {
        this.theEntity.getNavigator().setAvoidSun(true);
    }

    // JAVADOC METHOD $$ func_75251_c
    public void resetTask()
    {
        this.theEntity.getNavigator().setAvoidSun(false);
    }
}