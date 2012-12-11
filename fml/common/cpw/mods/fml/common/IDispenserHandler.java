package cpw.mods.fml.common;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 *
 * Deprecated without replacement. Use vanilla DispenserRegistry code.
 *
 * @author cpw
 *
 */
@Deprecated
public interface IDispenserHandler
{
    /**
     * Called to dispense an entity
     * @param x
     * @param y
     * @param z
     * @param xVelocity
     * @param zVelocity
     * @param world
     * @param item
     * @param random
     * @param entX
     * @param entY
     * @param entZ
     */
    int dispense(int x, int y, int z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY, double entZ);
}
