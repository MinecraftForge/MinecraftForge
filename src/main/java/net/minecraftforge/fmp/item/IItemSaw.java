package net.minecraftforge.fmp.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fmp.microblock.IMicroblock;

/**
 * Interface that represents an item which can cut blocks into microblocks. Can be added to an {@link Item} or {@link ItemStack} via
 * {@link Capability Capabilities}.<br/>
 * Although Forge Multipart doesn't include microblocks, other mods that add them should check for this interface in their recipes.
 * 
 * @see IMicroblock
 * @see CapabilityItemSaw
 */
public interface IItemSaw
{
    
    public int getSawStrength(ItemStack stack);
    
    public static class Implementation implements IItemSaw 
    {
        
        private final int strength;
        
        public Implementation(int strength)
        {
            this.strength = strength;
        }
        
        @Override
        public int getSawStrength(ItemStack stack)
        {
            return strength;
        }
        
    }

}
