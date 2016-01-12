package net.minecraftforge.common.capabilities;

import net.minecraft.util.EnumFacing;

public interface ICapabilityProvider
{
    /**
     * Determines if this object has support for the capability in question on the specific side.
     * The return value of this MIGHT change during runtime if this object gains or looses support
     * for a capability.
     *
     * Example:
     *   A Pipe getting a cover placed on one side causing it loose the Inventory attachment function for that side.
     *
     * This is a light weight version of getCapability, intended for metadata uses.
     *
     * @param capability The capability to check
     * @param facing The Side to check from:
     *   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability.
     */
    boolean hasCapability(Capability<?> capability, EnumFacing facing);

    /**
     * Retrieves the handler for the capability requested on the specific side.
     * The return value CAN be null if the object does not support the capability.
     * The return value CAN be the same for multiple faces.
     *
     * @param capability The capability to check
     * @param facing The Side to check from:
     *   CAN BE NULL. Null is defined to represent 'internal' or 'self'
     * @return True if this object supports the capability.
     */
    <T> T getCapability(Capability<T> capability, EnumFacing facing);
}
