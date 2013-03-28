package net.minecraftforge.inventory;

/**
 * Marker interface for different types of inventories. Implementations of this
 * should implement either IForgeCustomInventory or IForgeLinearInventory or both.
 * 
 * An inventory, as defined here, is an area which items can be inserted into
 * and extracted from by automation.
 * This differs from an IInventory inventory, which is a set of slots visible to a GUI
 * or to non-IForgeInventory-aware automation methods.
 * 
 * Unlike with IInventory, a block may expose a completely different inventory to each side.
 * They are not restricted to being contiguous ranges of a linear inventory as with Forge
 * ISidedInventory, or subsets of a linear inventory as with vanilla ISidedInventory.
 */
public interface IForgeInventory {
    
}
