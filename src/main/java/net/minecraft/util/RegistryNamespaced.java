package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced extends RegistrySimple implements IObjectIntIterable
{
    protected ObjectIntIdentityMap field_148759_a = new ObjectIntIdentityMap();
    protected final Map field_148758_b;
    private static final String __OBFID = "CL_00001206";

    public RegistryNamespaced()
    {
        this.field_148758_b = ((BiMap)this.registryObjects).inverse();
    }

    public void func_148756_a(int p_148756_1_, String p_148756_2_, Object p_148756_3_)
    {
        this.field_148759_a.func_148746_a(p_148756_3_, p_148756_1_);
        this.putObject(func_148755_c(p_148756_2_), p_148756_3_);
    }

    protected Map func_148740_a()
    {
        return HashBiMap.create();
    }

    public Object getObject(String p_148751_1_)
    {
        return super.getObject(func_148755_c(p_148751_1_));
    }

    public String func_148750_c(Object p_148750_1_)
    {
        return (String)this.field_148758_b.get(p_148750_1_);
    }

    public boolean func_148741_d(String p_148752_1_)
    {
        return super.func_148741_d(func_148755_c(p_148752_1_));
    }

    public int func_148757_b(Object p_148757_1_)
    {
        return this.field_148759_a.func_148747_b(p_148757_1_);
    }

    public Object func_148754_a(int p_148754_1_)
    {
        return this.field_148759_a.func_148745_a(p_148754_1_);
    }

    public Iterator iterator()
    {
        return this.field_148759_a.iterator();
    }

    public boolean func_148753_b(int p_148753_1_)
    {
        return this.field_148759_a.func_148744_b(p_148753_1_);
    }

    protected static String func_148755_c(String p_148755_0_)
    {
        return p_148755_0_.indexOf(58) == -1 ? "minecraft:" + p_148755_0_ : p_148755_0_;
    }

    public boolean func_148741_d(Object p_148741_1_)
    {
        return this.func_148741_d((String)p_148741_1_);
    }

    public Object getObject(Object par1Obj)
    {
        return this.getObject((String)par1Obj);
    }
}