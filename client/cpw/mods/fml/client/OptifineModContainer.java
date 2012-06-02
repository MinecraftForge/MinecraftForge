/*
 * The FML Forge Mod Loader suite.
 * Copyright (C) 2012 cpw
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

package cpw.mods.fml.client;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.common.FMLModContainer;
import cpw.mods.fml.common.IConsoleHandler;
import cpw.mods.fml.common.ICraftingHandler;
import cpw.mods.fml.common.IDispenseHandler;
import cpw.mods.fml.common.IKeyHandler;
import cpw.mods.fml.common.INetworkHandler;
import cpw.mods.fml.common.IPickupNotifier;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;

/**
 * @author cpw
 *
 */
public class OptifineModContainer extends FMLModContainer
{
    private String optifineVersion;
    private ModMetadata metadata;
    /**
     * @param optifineConfig
     */
    public OptifineModContainer(Class<?> optifineConfig)
    {
        super("Optifine");
        try
        {
            optifineVersion = (String) optifineConfig.getField("VERSION").get(null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getName()
    {
        return "Optifine";
    }
    @Override
    public String getVersion()
    {
        return optifineVersion;
    }

}
