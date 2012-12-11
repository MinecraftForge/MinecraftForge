/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */
package cpw.mods.fml.common;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;


/**
 * Deprecated without replacement, use vanilla DispenserRegistry code
 *
 * @author cpw
 *
 */
@Deprecated
public interface IDispenseHandler
{
    /**
     * Return -1 if you don't want to dispense anything. the other values seem to have specific meanings
     * to blockdispenser.
     *
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
    @Deprecated
    int dispense(double x, double y, double z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY, double entZ);
}
