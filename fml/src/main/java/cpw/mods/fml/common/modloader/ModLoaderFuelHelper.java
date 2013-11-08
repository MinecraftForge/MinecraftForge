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

import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.IFuelHandler;

public class ModLoaderFuelHelper implements IFuelHandler
{

    private BaseModProxy mod;

    public ModLoaderFuelHelper(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public int getBurnTime(ItemStack fuel)
    {
        return mod.addFuel(fuel.field_77993_c, fuel.func_77960_j());
    }

}
