/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraft.src.forge;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Entity;

public interface IEntityInteractHandler
{
    /**
     * This is called before a player attacks, or interacts {left or right click by default}
     * with another entity. Before any damage, or other interaction code is run.
     * In multiplayer, this is called by both the client and the server.
     * 
     * @param player The player doing the interacting
     * @param entity The entity being interacted with
     * @param isAttack True if it is a attack {left click} false if it is a interact {right click}
     * @return True to continue processing, false to cancel.
     */
    public boolean onEntityInteract(EntityPlayer player, Entity entity, boolean isAttack);
}