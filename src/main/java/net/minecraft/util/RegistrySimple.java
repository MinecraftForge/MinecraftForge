package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrySimple implements IRegistry
{
    private static final Logger field_148743_a = LogManager.getLogger();
    // JAVADOC FIELD $$ field_82596_a
    protected final Map registryObjects = this.func_148740_a();
    private static final String __OBFID = "CL_00001210";

    protected Map func_148740_a()
    {
        return Maps.newHashMap();
    }

    public Object getObject(Object par1Obj)
    {
        return this.registryObjects.get(par1Obj);
    }

    // JAVADOC METHOD $$ func_82595_a
    public void putObject(Object par1Obj, Object par2Obj)
    {
        if (this.registryObjects.containsKey(par1Obj))
        {
            field_148743_a.warn("Adding duplicate key \'" + par1Obj + "\' to registry");
        }

        this.registryObjects.put(par1Obj, par2Obj);
    }

    public Set func_148742_b()
    {
        return Collections.unmodifiableSet(this.registryObjects.keySet());
    }

    public boolean func_148741_d(Object p_148741_1_)
    {
        return this.registryObjects.containsKey(p_148741_1_);
    }
}