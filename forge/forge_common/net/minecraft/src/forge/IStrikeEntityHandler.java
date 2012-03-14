/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.ItemStack;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Entity;

public interface IStrikeEntityHandler
{

    /**
     * This is called after Minecraft after the player hits an entity.
     * @see MinecraftForge#registerStrikeEntityHandler(IStrikeEntityHandler)
     */
    public ItemStack strikeEntity(ItemStack stack, EntityPlayer player, Entity entity);

}
