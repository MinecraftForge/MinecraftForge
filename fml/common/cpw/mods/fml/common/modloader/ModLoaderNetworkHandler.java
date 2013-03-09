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

import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkModHandler;

public class ModLoaderNetworkHandler extends NetworkModHandler
{

    private BaseModProxy baseMod;
    public ModLoaderNetworkHandler(ModLoaderModContainer mlmc)
    {
        super(mlmc, null);
    }

    public void setBaseMod(BaseModProxy baseMod)
    {
        this.baseMod = baseMod;
    }

    @Override
    public boolean requiresClientSide()
    {
        return false;
    }

    @Override
    public boolean requiresServerSide()
    {
        return false;
    }

    @Override
    public boolean acceptVersion(String version)
    {
        return baseMod.getVersion().equals(version);
    }
    @Override
    public boolean isNetworkMod()
    {
        return true;
    }
}
