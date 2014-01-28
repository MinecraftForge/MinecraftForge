package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap
{
    protected final Map attributes = new HashMap();
    protected final Map attributesByName = new LowerStringMap();
    private static final String __OBFID = "CL_00001566";

    public IAttributeInstance getAttributeInstance(IAttribute par1Attribute)
    {
        return (IAttributeInstance)this.attributes.get(par1Attribute);
    }

    public IAttributeInstance getAttributeInstanceByName(String par1Str)
    {
        return (IAttributeInstance)this.attributesByName.get(par1Str);
    }

    public abstract IAttributeInstance func_111150_b(IAttribute var1);

    public Collection getAllAttributes()
    {
        return this.attributesByName.values();
    }

    public void func_111149_a(ModifiableAttributeInstance par1ModifiableAttributeInstance) {}

    public void removeAttributeModifiers(Multimap par1Multimap)
    {
        Iterator iterator = par1Multimap.entries().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            IAttributeInstance iattributeinstance = this.getAttributeInstanceByName((String)entry.getKey());

            if (iattributeinstance != null)
            {
                iattributeinstance.removeModifier((AttributeModifier)entry.getValue());
            }
        }
    }

    public void applyAttributeModifiers(Multimap par1Multimap)
    {
        Iterator iterator = par1Multimap.entries().iterator();

        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            IAttributeInstance iattributeinstance = this.getAttributeInstanceByName((String)entry.getKey());

            if (iattributeinstance != null)
            {
                iattributeinstance.removeModifier((AttributeModifier)entry.getValue());
                iattributeinstance.applyModifier((AttributeModifier)entry.getValue());
            }
        }
    }
}