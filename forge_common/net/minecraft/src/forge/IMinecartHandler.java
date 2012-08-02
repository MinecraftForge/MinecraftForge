package net.minecraft.src.forge;

import net.minecraft.src.Entity;
import net.minecraft.src.EntityMinecart;
import net.minecraft.src.EntityPlayer;

public interface IMinecartHandler
{

    /**
     * This functions is called at the end of every minecart's doUpdate loop.
     * If you override EntityMinecart.doUpdate(), is recommended that you retain the code that calls this function.
     * @param cart The cart that called the function.
     * @param i X coordinate of the rail
     * @param j Y coordinate of the rail
     * @param k Z coordinate of the rail
     */
    public void onMinecartUpdate(EntityMinecart minecart, int x, int y, int z);

    /**
     * This function allows several mods to add code into the collision routine at the same time regardless of the collision handler registered.
     * If you override EntityMinecart.applyEntityCollision(), is recommended that you retain the code that calls this function.
     * @param cart The cart that called the function.
     * @param other
     */
    public void onMinecartEntityCollision(EntityMinecart minecart, Entity entity);

    /**
     * This function is called whenever a player attempts to interact with a minecart.
     * The primary reason for this hook is to fix a few bugs and put restrictions on how a minecart can be used under certain circumstances.
     * If you override EntityMinecart.interact(), is recommended that you retain the code that calls this function.
     * @param cart The cart that called the function.
     * @param player The player that tried to interact with the minecart.
     * @param canceled Wither or not a pervious hook has canceled the interaction of a player.
     * @return Whether the player can interact with the minecart.
     */
    public boolean onMinecartInteract(EntityMinecart minecart, EntityPlayer player, boolean canceled);

}
