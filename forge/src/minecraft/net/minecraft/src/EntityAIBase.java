package net.minecraft.src;

public abstract class EntityAIBase
{
    /**
     * A bitmask telling which other tasks may not run concurrently. The test is a simple bitwise AND - if it yields
     * zero, the two tasks may run concurrently, if not - they must run exclusively from each other.
     */
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

    /**
     * Returns whether the task requires multiple updates or not
     */
    public boolean isContinuous()
    {
        return true;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {}

    /**
     * Resets the task
     */
    public void resetTask() {}

    /**
     * Updates the task
     */
    public void updateTask() {}

    /**
     * Sets a bitmask telling which other tasks may not run concurrently. The test is a simple bitwise AND - if it
     * yields zero, the two tasks may run concurrently, if not - they must run exclusively from each other.
     */
    public void setMutexBits(int par1)
    {
        this.mutexBits = par1;
    }

    /**
     * Get a bitmask telling which other tasks may not run concurrently. The test is a simple bitwise AND - if it yields
     * zero, the two tasks may run concurrently, if not - they must run exclusively from each other.
     */
    public int getMutexBits()
    {
        return this.mutexBits;
    }
}
