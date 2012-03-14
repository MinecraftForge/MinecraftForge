package net.minecraft.src;

public class EntityAIDefendVillage extends EntityAITarget
{
    EntityIronGolem irongolem;
    EntityLiving field_48384_b;

    public EntityAIDefendVillage(EntityIronGolem par1EntityIronGolem)
    {
        super(par1EntityIronGolem, 16.0F, false, true);
        this.irongolem = par1EntityIronGolem;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        Village var1 = this.irongolem.getVillage();

        if (var1 == null)
        {
            return false;
        }
        else
        {
            this.field_48384_b = var1.findNearestVillageAggressor(this.irongolem);
            return this.func_48376_a(this.field_48384_b, false);
        }
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
        this.irongolem.func_48092_c(this.field_48384_b);
        super.startExecuting();
    }
}
