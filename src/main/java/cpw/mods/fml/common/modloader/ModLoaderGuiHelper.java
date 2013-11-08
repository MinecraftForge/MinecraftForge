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

import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;

import com.google.common.collect.Sets;

import cpw.mods.fml.common.network.IGuiHandler;

public class ModLoaderGuiHelper implements IGuiHandler
{

    private BaseModProxy mod;
    private Set<Integer> ids;
    private Container container;
    private int currentID;

    ModLoaderGuiHelper(BaseModProxy mod)
    {
        this.mod = mod;
        this.ids = Sets.newHashSet();
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return container;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        return ModLoaderHelper.getClientSideGui(mod, player, ID, x, y, z);
    }

    public void injectContainerAndID(Container container, int ID)
    {
        this.container = container;
        this.currentID = ID;
    }

    public Object getMod()
    {
        return mod;
    }

    public void associateId(int additionalID)
    {
        this.ids.add(additionalID);
    }

}
