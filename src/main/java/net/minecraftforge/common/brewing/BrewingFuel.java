package net.minecraftforge.common.brewing;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnegative;

/**
 * A capability interface to access the brewing fuel value of an item stack.
 * Do not implement this method for an item; it will not work!
 *
 * <p>To add brewing fuel capability to your item stack, override
 * {@link Item#initCapabilities(ItemStack, NBTTagCompound)}.</p>
 *
 * <p>To add or override brewing fuel capabilities to other mods' item stacks,
 * subscribe to {@link net.minecraftforge.event.AttachCapabilitiesEvent} with
 * {@link ItemStack} as its generic type.</p>
 */
@FunctionalInterface
public interface BrewingFuel
{
    /**
     * Gets the fuel value bound on this capability.
     *
     * If the fuel value is not positive, the fuel does not work.
     *
     * @return The fuel value
     */
    @Nonnegative
    int getFuelValue();
}
