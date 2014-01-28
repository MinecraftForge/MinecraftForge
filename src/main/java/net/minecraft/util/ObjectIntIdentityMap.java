package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import gnu.trove.map.hash.TIntIntHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjectIntIdentityMap implements IObjectIntIterable
{
    protected TIntIntHashMap field_148749_a = new TIntIntHashMap(256, 0.5F, -1, -1);
    protected List field_148748_b = new ArrayList();
    private static final String __OBFID = "CL_00001203";

    public void func_148746_a(Object p_148746_1_, int p_148746_2_)
    {
        this.field_148749_a.put(System.identityHashCode(p_148746_1_), p_148746_2_);

        while (this.field_148748_b.size() <= p_148746_2_)
        {
            this.field_148748_b.add((Object)null);
        }

        this.field_148748_b.set(p_148746_2_, p_148746_1_);
    }

    public int func_148747_b(Object p_148747_1_)
    {
        return this.field_148749_a.get(System.identityHashCode(p_148747_1_));
    }

    public Object func_148745_a(int p_148745_1_)
    {
        return p_148745_1_ >= 0 && p_148745_1_ < this.field_148748_b.size() ? this.field_148748_b.get(p_148745_1_) : null;
    }

    public Iterator iterator()
    {
        return Iterators.filter(this.field_148748_b.iterator(), Predicates.notNull());
    }

    public boolean func_148744_b(int p_148744_1_)
    {
        return this.func_148745_a(p_148744_1_) != null;
    }
}