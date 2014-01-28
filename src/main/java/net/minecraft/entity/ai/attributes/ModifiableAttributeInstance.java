package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance
{
    private final BaseAttributeMap field_111138_a;
    private final IAttribute field_111136_b;
    private final Map field_111137_c = Maps.newHashMap();
    private final Map field_111134_d = Maps.newHashMap();
    private final Map field_111135_e = Maps.newHashMap();
    private double baseValue;
    private boolean field_111133_g = true;
    private double field_111139_h;
    private static final String __OBFID = "CL_00001567";

    public ModifiableAttributeInstance(BaseAttributeMap par1BaseAttributeMap, IAttribute par2Attribute)
    {
        this.field_111138_a = par1BaseAttributeMap;
        this.field_111136_b = par2Attribute;
        this.baseValue = par2Attribute.getDefaultValue();

        for (int i = 0; i < 3; ++i)
        {
            this.field_111137_c.put(Integer.valueOf(i), new HashSet());
        }
    }

    public IAttribute func_111123_a()
    {
        return this.field_111136_b;
    }

    public double getBaseValue()
    {
        return this.baseValue;
    }

    public void setAttribute(double par1)
    {
        if (par1 != this.getBaseValue())
        {
            this.baseValue = par1;
            this.func_111131_f();
        }
    }

    public Collection func_111130_a(int par1)
    {
        return (Collection)this.field_111137_c.get(Integer.valueOf(par1));
    }

    public Collection func_111122_c()
    {
        HashSet hashset = new HashSet();

        for (int i = 0; i < 3; ++i)
        {
            hashset.addAll(this.func_111130_a(i));
        }

        return hashset;
    }

    // JAVADOC METHOD $$ func_111127_a
    public AttributeModifier getModifier(UUID par1UUID)
    {
        return (AttributeModifier)this.field_111135_e.get(par1UUID);
    }

    public void applyModifier(AttributeModifier par1AttributeModifier)
    {
        if (this.getModifier(par1AttributeModifier.getID()) != null)
        {
            throw new IllegalArgumentException("Modifier is already applied on this attribute!");
        }
        else
        {
            Object object = (Set)this.field_111134_d.get(par1AttributeModifier.getName());

            if (object == null)
            {
                object = new HashSet();
                this.field_111134_d.put(par1AttributeModifier.getName(), object);
            }

            ((Set)this.field_111137_c.get(Integer.valueOf(par1AttributeModifier.getOperation()))).add(par1AttributeModifier);
            ((Set)object).add(par1AttributeModifier);
            this.field_111135_e.put(par1AttributeModifier.getID(), par1AttributeModifier);
            this.func_111131_f();
        }
    }

    private void func_111131_f()
    {
        this.field_111133_g = true;
        this.field_111138_a.func_111149_a(this);
    }

    public void removeModifier(AttributeModifier par1AttributeModifier)
    {
        for (int i = 0; i < 3; ++i)
        {
            Set set = (Set)this.field_111137_c.get(Integer.valueOf(i));
            set.remove(par1AttributeModifier);
        }

        Set set1 = (Set)this.field_111134_d.get(par1AttributeModifier.getName());

        if (set1 != null)
        {
            set1.remove(par1AttributeModifier);

            if (set1.isEmpty())
            {
                this.field_111134_d.remove(par1AttributeModifier.getName());
            }
        }

        this.field_111135_e.remove(par1AttributeModifier.getID());
        this.func_111131_f();
    }

    @SideOnly(Side.CLIENT)
    public void func_142049_d()
    {
        Collection collection = this.func_111122_c();

        if (collection != null)
        {
            ArrayList arraylist = new ArrayList(collection);
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext())
            {
                AttributeModifier attributemodifier = (AttributeModifier)iterator.next();
                this.removeModifier(attributemodifier);
            }
        }
    }

    public double getAttributeValue()
    {
        if (this.field_111133_g)
        {
            this.field_111139_h = this.func_111129_g();
            this.field_111133_g = false;
        }

        return this.field_111139_h;
    }

    private double func_111129_g()
    {
        double d0 = this.getBaseValue();
        AttributeModifier attributemodifier;

        for (Iterator iterator = this.func_111130_a(0).iterator(); iterator.hasNext(); d0 += attributemodifier.getAmount())
        {
            attributemodifier = (AttributeModifier)iterator.next();
        }

        double d1 = d0;
        Iterator iterator1;
        AttributeModifier attributemodifier1;

        for (iterator1 = this.func_111130_a(1).iterator(); iterator1.hasNext(); d1 += d0 * attributemodifier1.getAmount())
        {
            attributemodifier1 = (AttributeModifier)iterator1.next();
        }

        for (iterator1 = this.func_111130_a(2).iterator(); iterator1.hasNext(); d1 *= 1.0D + attributemodifier1.getAmount())
        {
            attributemodifier1 = (AttributeModifier)iterator1.next();
        }

        return this.field_111136_b.clampValue(d1);
    }
}