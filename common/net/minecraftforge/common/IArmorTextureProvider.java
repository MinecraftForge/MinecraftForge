/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.common;

import net.minecraft.item.ItemStack;

/**
 * This interface has to be implemented by an instance of ItemArmor.
 * It allows for the application of a custom texture file to the player skin
 * when the armor is worn.
 */
public interface IArmorTextureProvider
{

    /**
     * This interface has to return the path to a file that is the same
     * format as iron_1.png (or any of the other armor files). It will be
     * applied to the player skin when the armor is worn.
     */
    public String getArmorTextureFile(ItemStack itemstack);

}

