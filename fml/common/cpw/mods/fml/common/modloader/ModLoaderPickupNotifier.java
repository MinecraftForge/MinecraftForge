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

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.IPickupNotifier;

public class ModLoaderPickupNotifier implements IPickupNotifier
{

    private BaseModProxy mod;

    public ModLoaderPickupNotifier(BaseModProxy mod)
    {
        this.mod = mod;
    }

    @Override
    public void notifyPickup(EntityItem item, EntityPlayer player)
    {
        mod.onItemPickup(player, item.func_92059_d());
    }

}
