package net.minecraftforge.common.capabilities.work;


/**
 * Capability instance for targets that are able to
 * work on something and have their state detected.
 * This capability does not store anything itself,
 * the information should be supplied by its owner.
 * 
 * For this implementation to be sane, it is expected
 * that when hasWork() and canWork() are both true,
 * that the target is actually performing its work.
 *  
 * @author rubensworks
 */
public interface IWorker
{
    /**
     * If the target has a valid input for things to process.
     * In case of the vanilla furnace, this will return true if there
     * is a valid smeltable in its input slot.
     **/
    boolean hasWork();
    
    /**
     * If the target is able to start working in its current state,
     * and if it is not being blocked by some external or internal factor.
     * In case of the vanilla furnace, this will return true if there is enough
     * fuel in its fuel slot.
     **/
    boolean canWork();
}
