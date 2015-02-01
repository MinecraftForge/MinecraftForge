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

import java.util.Map;

import net.minecraft.nbt.*;
import net.minecraft.world.storage.*;


public interface WorldAccessContainer
{
    public NBTTagCompound getDataForWriting(SaveHandler handler, WorldInfo info);
    public void readData(SaveHandler handler, WorldInfo info, Map<String,NBTBase> propertyMap, NBTTagCompound tag);
}
