package net.minecraft.src;

import java.util.ArrayList;

public class EntitySenses
{
    EntityLiving field_48550_a;
    ArrayList field_48548_b = new ArrayList();
    ArrayList field_48549_c = new ArrayList();

    public EntitySenses(EntityLiving par1EntityLiving)
    {
        this.field_48550_a = par1EntityLiving;
    }

    public void func_48547_a()
    {
        this.field_48548_b.clear();
        this.field_48549_c.clear();
    }

    public boolean func_48546_a(Entity par1Entity)
    {
        if (this.field_48548_b.contains(par1Entity))
        {
            return true;
        }
        else if (this.field_48549_c.contains(par1Entity))
        {
            return false;
        }
        else
        {
            Profiler.startSection("canSee");
            boolean var2 = this.field_48550_a.canEntityBeSeen(par1Entity);
            Profiler.endSection();

            if (var2)
            {
                this.field_48548_b.add(par1Entity);
            }
            else
            {
                this.field_48549_c.add(par1Entity);
            }

            return var2;
        }
    }
}
