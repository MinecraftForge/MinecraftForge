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

package net.minecraftforge.fml.common;

import net.minecraft.item.ItemStack;

public interface IFuelHandler
{
    /**
     * Returns the fuel value of the item stack if this is a fuel.
     * Returns a negative integer to disallow consuming this item stack.
     * By default, you should return 0.
     * <br>
     * To override other mods' behavior, register your fuel handler after the other mod did.
     * To override vanilla behavior just register your fuel handler.
     * <br>
     * 
     * @param fuel the item stack
     * @return the fuel value of the item stack, other cases see above
     */
    int getBurnTime(ItemStack fuel);
}
