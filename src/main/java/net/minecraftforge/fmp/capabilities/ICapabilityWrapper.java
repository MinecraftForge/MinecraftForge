package net.minecraftforge.fmp.capabilities;

import java.util.Collection;

import net.minecraftforge.common.capabilities.Capability;

/**
 * Interface that allows mods to provide an implementation of a capability in the case that there's more than one implementation
 * for one side of a block (multiparts).
 * 
 * @see Capability
 * @see CapabilityWrapperRegistry
 */
public interface ICapabilityWrapper<T>
{
    
    public T wrapImplementations(Collection<T> implementations);

}
