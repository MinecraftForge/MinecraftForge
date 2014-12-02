package net.minecraftforge.event.entity.player;

import cpw.mods.fml.common.eventhandler.Cancelable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** 
 * Fired when a item is enchant on a enchant table.
 *  This event is fired in {@link ContainerEnchantment#enchantItem(EntityPlayer, int)} after item enchantment.
 */
@Cancelable
public class ItemEnchantEvent extends PlayerEvent 
{
    /**
     * The item with enchantment.
     */
    public final ItemStack enchanted;
    
    /**
     * The inventory of the Enchanting Table containing the table slot.
     */
    public final IInventory tableInventory;
    
    public ItemEnchantEvent(EntityPlayer player, ItemStack enchanted, IInventory tableInventory)
    {
        super(player);
        this.enchanted = enchanted;
        this.tableInventory = tableInventory;
    }
}