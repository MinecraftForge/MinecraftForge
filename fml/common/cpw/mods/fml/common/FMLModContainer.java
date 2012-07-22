/*
 * The FML Forge Mod Loader suite. Copyright (C) 2012 cpw
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
package cpw.mods.fml.common;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.discovery.ContainerType;
import cpw.mods.fml.common.event.FMLConstructionEvent;

public class FMLModContainer implements ModContainer
{
    private Mod modDescriptor;
    private Object modInstance;
    private File source;
    private ModMetadata modMetadata;
    private String className;
    private String modId;
    private Map<String, Object> descriptor;
    private boolean enabled;
    private List<String> requirements;
    private List<String> dependencies;
    private List<String> dependants;
    private boolean overridesMetadata;
    private EventBus eventBus;
    private LoadController controller;

    public FMLModContainer(String className, File modSource, Map<String,Object> modDescriptor)
    {
        this.className = className;
        this.source = modSource;
        this.descriptor = modDescriptor;
    }

    @Override
    public String getModId()
    {
        return (String) descriptor.get("modid");
    }

    @Override
    public String getName()
    {
        return modMetadata.name;
    }

    @Override
    public String getVersion()
    {
        return modMetadata.version;
    }

    @Override
    public File getSource()
    {
        return source;
    }

    @Override
    public ModMetadata getMetadata()
    {
        return modMetadata;
    }

    @Override
    public void bindMetadata(MetadataCollection mc)
    {
        modMetadata = mc.getMetadataForId(getModId());
        
        if (descriptor.containsKey("usesMetadata"))
        {
            overridesMetadata = !((Boolean)descriptor.get("usesMetadata")).booleanValue(); 
        }
        
        if (overridesMetadata || !modMetadata.useDependencyInformation)
        {
            List<String> requirements = Lists.newArrayList();
            List<String> dependencies = Lists.newArrayList();
            List<String> dependants = Lists.newArrayList();
            Loader.instance().computeDependencies((String) descriptor.get("dependencies"), requirements, dependencies, dependants);
            modMetadata.requiredMods = requirements;
            modMetadata.dependencies = dependencies;
            modMetadata.dependants = dependants;
        }
    }

    @Override
    public void setEnabledState(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public List<String> getRequirements()
    {
        return modMetadata.requiredMods;
    }

    @Override
    public List<String> getDependencies()
    {
        return modMetadata.dependencies;
    }

    @Override
    public List<String> getDependants()
    {
        return modMetadata.dependants;
    }

    @Override
    public String getSortingRules()
    {
        return modMetadata.printableSortingRules();
    }

    @Override
    public boolean matches(Object mod)
    {
        return mod == modInstance;
    }

    @Override
    public Object getMod()
    {
        return modInstance;
    }

    @Override
    public ProxyInjector findSidedProxy()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
        if (this.enabled)
        {
            this.eventBus = bus;
            this.controller = controller;
            eventBus.register(this);
            return true;
        }
        else
        {
            return false;
        }
    }
    
    @Subscribe
    void constructMod(FMLConstructionEvent event)
    {
        try 
        {
            ModClassLoader modClassLoader = event.getModClassLoader();
            modClassLoader.addFile(source);
            modInstance = Class.forName(className, true, modClassLoader).newInstance();
        }
        catch (Exception e)
        {
            controller.errorOccurred(this, e);
            Throwables.propagateIfPossible(e);
        }
    }
}
