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

package cpw.mods.fml.common.modloader;

import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IDispenserHandler;

public class ModLoaderDispenseHelper implements IDispenserHandler
{

    private BaseModProxy mod;

    public ModLoaderDispenseHelper(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public int dispense(int x, int y, int z, int xVelocity, int zVelocity, World world, ItemStack item, Random random, double entX, double entY,
            double entZ)
    {
    	return -1;
    }

}
