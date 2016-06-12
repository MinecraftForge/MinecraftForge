package net.minecraftforge.fmp.capabilities;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class CapabilityWrapperRegistry
{
    
    private static Map<String, ICapabilityWrapper<?>> wrappers = new IdentityHashMap<String, ICapabilityWrapper<?>>();

    public static <T> void registerCapabilityWrapper(Capability<T> capability, ICapabilityWrapper<T> wrapper)
    {
        Preconditions.checkNotNull(capability, "Attempted to register a wrapper for a null capability!");
        Preconditions.checkNotNull(wrapper, "Attempted to register a null capability wrapper!");
        wrappers.put(capability.getName(), wrapper);
    }

    @SuppressWarnings("unchecked")
    public static <T> T wrap(Capability<T> capability, Collection<T> implementations)
    {
        if (!implementations.isEmpty())
        {
            ICapabilityWrapper<T> wrapper = (ICapabilityWrapper<T>) wrappers.get(capability.getName());
            if (wrapper != null)
            {
                return wrapper.wrapImplementations(implementations);
            }
        }
        return null;
    }

    static
    {
        registerCapabilityWrapper(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, new CapabilityWrapperItemHandler());
    }

}
