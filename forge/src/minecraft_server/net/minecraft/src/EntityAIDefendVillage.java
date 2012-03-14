package net.minecraft.src;

public class EntityAIDefendVillage extends EntityAITarget
{
    EntityIronGolem irongolem;
    EntityLiving field_48301_b;

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
            this.field_48301_b = var1.findNearestVillageAggressor(this.irongolem);
            return this.func_48284_a(this.field_48301_b, false);
        }
    }

    public void startExecuting()
    {
        this.irongolem.func_48327_b(this.field_48301_b);
        super.startExecuting();
    }
}
