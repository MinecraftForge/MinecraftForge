package net.minecraft.entity.ai;

public abstract class EntityAIBase
{
    // JAVADOC FIELD $$ field_75254_a
    private int mutexBits;
    private static final String __OBFID = "CL_00001587";

    // JAVADOC METHOD $$ func_75250_a
    public abstract boolean shouldExecute();

    // JAVADOC METHOD $$ func_75253_b
    public boolean continueExecuting()
    {
        return this.shouldExecute();
    }

    // JAVADOC METHOD $$ func_75252_g
    public boolean isInterruptible()
    {
        return true;
    }

    // JAVADOC METHOD $$ func_75249_e
    public void startExecuting() {}

    // JAVADOC METHOD $$ func_75251_c
    public void resetTask() {}

    // JAVADOC METHOD $$ func_75246_d
    public void updateTask() {}

    // JAVADOC METHOD $$ func_75248_a
    public void setMutexBits(int par1)
    {
        this.mutexBits = par1;
    }

    // JAVADOC METHOD $$ func_75247_h
    public int getMutexBits()
    {
        return this.mutexBits;
    }
}