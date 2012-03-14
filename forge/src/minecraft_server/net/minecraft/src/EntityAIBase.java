package net.minecraft.src;

public abstract class EntityAIBase
{
    private int mutexBits = 0;

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public abstract boolean shouldExecute();

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.shouldExecute();
    }

    public boolean isContinuous()
    {
        return true;
    }

    public void startExecuting() {}

    public void resetTask() {}

    public void updateTask() {}

    public void setMutexBits(int par1)
    {
        this.mutexBits = par1;
    }

    public int getMutexBits()
    {
        return this.mutexBits;
    }
}
