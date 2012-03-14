package net.minecraft.src;

import java.util.Comparator;

public class EntityAINearestAttackableTargetSorter implements Comparator
{
    private Entity field_48470_b;

    final EntityAINearestAttackableTarget field_48471_a;

    public EntityAINearestAttackableTargetSorter(EntityAINearestAttackableTarget par1EntityAINearestAttackableTarget, Entity par2Entity)
    {
        this.field_48471_a = par1EntityAINearestAttackableTarget;
        this.field_48470_b = par2Entity;
    }

    public int func_48469_a(Entity par1Entity, Entity par2Entity)
    {
        double var3 = this.field_48470_b.getDistanceSqToEntity(par1Entity);
        double var5 = this.field_48470_b.getDistanceSqToEntity(par2Entity);
        return var3 < var5 ? -1 : (var3 > var5 ? 1 : 0);
    }

    public int compare(Object par1Obj, Object par2Obj)
    {
        return this.func_48469_a((Entity)par1Obj, (Entity)par2Obj);
    }
}
