/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 * 
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import net.minecraft.item.ItemStack;

public interface IFuelHandler
{
    int getBurnTime(ItemStack fuel);
}
