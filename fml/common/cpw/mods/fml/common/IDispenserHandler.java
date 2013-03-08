/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     cpw - implementation
 */

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
