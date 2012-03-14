package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;

public class EntityAITasks
{
    private ArrayList tasksToDo = new ArrayList();

    /** Tasks currently being executed */
    private ArrayList executingTasks = new ArrayList();

    public void addTask(int par1, EntityAIBase par2EntityAIBase)
    {
        this.tasksToDo.add(new EntityAITaskEntry(this, par1, par2EntityAIBase));
    }

    public void onUpdateTasks()
    {
        ArrayList var1 = new ArrayList();
        Iterator var2 = this.tasksToDo.iterator();

        while (var2.hasNext())
        {
            EntityAITaskEntry var3 = (EntityAITaskEntry)var2.next();
            boolean var4 = this.executingTasks.contains(var3);

            if (var4)
            {
                if (this.func_46116_a(var3) && var3.action.continueExecuting())
                {
                    continue;
                }

                var3.action.resetTask();
                this.executingTasks.remove(var3);
            }

            if (this.func_46116_a(var3) && var3.action.shouldExecute())
            {
                var1.add(var3);
                this.executingTasks.add(var3);
            }
        }

        boolean var5 = false;

        if (var5 && var1.size() > 0)
        {
            System.out.println("Starting: ");
        }

        Iterator var6;
        EntityAITaskEntry var7;

        for (var6 = var1.iterator(); var6.hasNext(); var7.action.startExecuting())
        {
            var7 = (EntityAITaskEntry)var6.next();

            if (var5)
            {
                System.out.println(var7.action.toString() + ", ");
            }
        }

        if (var5 && this.executingTasks.size() > 0)
        {
            System.out.println("Running: ");
        }

        for (var6 = this.executingTasks.iterator(); var6.hasNext(); var7.action.updateTask())
        {
            var7 = (EntityAITaskEntry)var6.next();

            if (var5)
            {
                System.out.println(var7.action.toString());
            }
        }
    }

    private boolean func_46116_a(EntityAITaskEntry par1EntityAITaskEntry)
    {
        Iterator var2 = this.tasksToDo.iterator();

        while (var2.hasNext())
        {
            EntityAITaskEntry var3 = (EntityAITaskEntry)var2.next();

            if (var3 != par1EntityAITaskEntry)
            {
                if (par1EntityAITaskEntry.priority >= var3.priority)
                {
                    if (this.executingTasks.contains(var3) && !this.areTasksCompatible(par1EntityAITaskEntry, var3))
                    {
                        return false;
                    }
                }
                else if (this.executingTasks.contains(var3) && !var3.action.isContinuous())
                {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Returns whether two EntityAITaskEntries can be executed concurrently
     */
    private boolean areTasksCompatible(EntityAITaskEntry par1EntityAITaskEntry, EntityAITaskEntry par2EntityAITaskEntry)
    {
        return (par1EntityAITaskEntry.action.getMutexBits() & par2EntityAITaskEntry.action.getMutexBits()) == 0;
    }
}
