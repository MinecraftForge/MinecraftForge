package net.minecraftforge.common.extensions;

import net.minecraft.core.HolderSet;

public interface IForgeHolderSet<T>
{
    default public HolderSet<T> self()
    {
        return (HolderSet<T>) this;
    }
    
    /**
     * <p>Adds a callback to run when this holderset's contents invalidate (i.e. because tags were rebound).
     * The intended usage and use case is with composite holdersets that need to cache sets/list based on other
     * holdersets, which may be mutable (because they are tag-based or themselves composite holdersets).
     * Composite holdersets should use this to add callbacks to each of their component holdersets when constructed.</p>
     * 
     * @param runnable Runnable to invoke when this component holderset's contents are no longer valid.
     * This runnable should only clear caches and allow them to be lazily reevaluated later,
     * as not all tag holdersets may have been rebound when this is called.
     * This runnable should also invalidate all of the caller's listeners.
     */
    default public void addInvalidationListener(Runnable runnable)
    {
        // noop by default, mutable/composite holdersets must override
    }
}
