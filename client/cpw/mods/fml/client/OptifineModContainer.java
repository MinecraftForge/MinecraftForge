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
public class OptifineModContainer implements ModContainer
{
    private String optifineVersion;
    private ModMetadata metadata;
    /**
     * @param optifineConfig
     */
    public OptifineModContainer(Class<?> optifineConfig)
    {
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
    public boolean wantsPreInit()
    {
        return false;
    }

    @Override
    public boolean wantsPostInit()
    {
        return false;
    }

    @Override
    public void preInit()
    {

    }

    @Override
    public void init()
    {

    }

    @Override
    public void postInit()
    {

    }

    @Override
    public String getName()
    {
        return "Optifine";
    }

    @Override
    public ModState getModState()
    {
        return ModState.AVAILABLE;
    }

    @Override
    public void nextState()
    {

    }

    @Override
    public void tickStart(TickType tick, Object... data)
    {

    }

    @Override
    public void tickEnd(TickType tick, Object... data)
    {

    }

    @Override
    public boolean matches(Object mod)
    {
        return false;
    }

    @Override
    public File getSource()
    {
        return new File(".");
    }

    @Override
    public String getSortingRules()
    {
        return "";
    }

    @Override
    public Object getMod()
    {
        return null;
    }

    @Override
    public boolean generatesWorld()
    {
        return false;
    }

    @Override
    public IWorldGenerator getWorldGenerator()
    {
        return null;
    }

    @Override
    public int lookupFuelValue(int itemId, int itemDamage)
    {
        return 0;
    }

    @Override
    public boolean wantsPickupNotification()
    {
        return false;
    }

    @Override
    public IPickupNotifier getPickupNotifier()
    {
        return null;
    }

    @Override
    public boolean wantsToDispense()
    {
        return false;
    }

    @Override
    public IDispenseHandler getDispenseHandler()
    {
        return null;
    }

    @Override
    public boolean wantsCraftingNotification()
    {
        return false;
    }

    @Override
    public ICraftingHandler getCraftingHandler()
    {
        return null;
    }

    @Override
    public List<String> getDependencies()
    {
        return Collections.emptyList();
    }

    @Override
    public List<String> getPreDepends()
    {
        return Collections.emptyList();
    }

    @Override
    public List<String> getPostDepends()
    {
        return Collections.emptyList();
    }

    @Override
    public boolean wantsNetworkPackets()
    {
        return false;
    }

    @Override
    public INetworkHandler getNetworkHandler()
    {
        return null;
    }

    @Override
    public boolean ownsNetworkChannel(String channel)
    {
        return false;
    }

    @Override
    public boolean wantsConsoleCommands()
    {
        return false;
    }

    @Override
    public IConsoleHandler getConsoleHandler()
    {
        return null;
    }

    @Override
    public boolean wantsPlayerTracking()
    {
        return false;
    }

    @Override
    public IPlayerTracker getPlayerTracker()
    {
        return null;
    }

    @Override
    public List<IKeyHandler> getKeys()
    {
        return null;
    }

    @Override
    public SourceType getSourceType()
    {
        return SourceType.CLASSPATH;
    }

    @Override
    public void setSourceType(SourceType type)
    {

    }

    @Override
    public ModMetadata getMetadata()
    {
        return metadata;
    }

    @Override
    public void setMetadata(ModMetadata meta)
    {
        this.metadata=meta;
    }

    @Override
    public void gatherRenderers(Map renderers)
    {

    }

    @Override
    public void requestAnimations()
    {

    }

    @Override
    public String getVersion()
    {
        return optifineVersion;
    }

}
