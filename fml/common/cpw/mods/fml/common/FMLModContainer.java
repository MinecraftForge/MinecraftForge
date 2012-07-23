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
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.LoaderState.ModState;
import cpw.mods.fml.common.Mod.Block;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.discovery.ContainerType;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class FMLModContainer implements ModContainer
{
    private Mod modDescriptor;
    private Object modInstance;
    private File source;
    private ModMetadata modMetadata;
    private String className;
    private String modId;
    private Map<String, Object> descriptor;
    private boolean enabled = true;
    private List<String> requirements;
    private List<String> dependencies;
    private List<String> dependants;
    private boolean overridesMetadata;
    private EventBus eventBus;
    private LoadController controller;
    private Multimap<Class<? extends Annotation>, Object> annotations;

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
        modMetadata = mc.getMetadataForId(getModId(), descriptor);
        
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
            FMLLog.fine("Enabling mod %s", getModId());
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
    
    private Multimap<Class<? extends Annotation>, Object> gatherAnnotations(Class<?> clazz) throws Exception
    {
        Multimap<Class<? extends Annotation>,Object> anns = ArrayListMultimap.create();
        
        for (Field f : clazz.getDeclaredFields())
        {
            for (Annotation a : f.getAnnotations())
            {
                f.setAccessible(true);
                anns.put(a.annotationType(), f);
            }
        }
        
        for (Method m : clazz.getDeclaredMethods())
        {
            for (Annotation a : m.getAnnotations())
            {
                if (m.getParameterTypes().length==0)
                {
                    m.setAccessible(true);
                    anns.put(a.annotationType(), m);
                }
                else
                {
                    FMLLog.severe("The mod type %s appears to have an invalid method annotation %s. This annotation can only apply to zero argument methods", getModId(), a.annotationType().getSimpleName());
                }
            }
        }
        return anns;
    }

    private void processFieldAnnotations() throws Exception
    {
        // Instance annotation
        for (Object o : annotations.get(Instance.class))
        {
            Field f = (Field) o;
            f.set(modInstance, modInstance);
        }
        
        for (Object o : annotations.get(Metadata.class))
        {
            Field f = (Field) o;
            f.set(modInstance, modMetadata);
        }
        
        for (Object o : annotations.get(Block.class))
        {
            Field f = (Field) o;
        }
    }

    @Subscribe
    public void constructMod(FMLConstructionEvent event)
    {
        try 
        {
            ModClassLoader modClassLoader = event.getModClassLoader();
            modClassLoader.addFile(source);
            Class<?> clazz = Class.forName(className, true, modClassLoader);
            annotations = gatherAnnotations(clazz);
            modInstance = clazz.newInstance();
            processFieldAnnotations();
        }
        catch (Throwable e)
        {
            controller.errorOccurred(this, e);
            Throwables.propagateIfPossible(e);
        }
    }
    
    @Subscribe
    public void preInitMod(FMLPreInitializationEvent event)
    {
        try
        {
            for (Object o : annotations.get(Mod.PreInit.class))
            {
                Method m = (Method) o;
                m.invoke(modInstance);
            }
        }
        catch (Throwable t)
        {
            controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
        }
    }
    
    @Subscribe
    public void initMod(FMLInitializationEvent event)
    {
        try
        {
            for (Object o : annotations.get(Mod.Init.class))
            {
                Method m = (Method) o;
                m.invoke(modInstance);
            }
        }
        catch (Throwable t)
        {
            controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
        }
    }
    
    @Subscribe
    public void postInitMod(FMLPostInitializationEvent event)
    {
        try
        {
            for (Object o : annotations.get(Mod.PostInit.class))
            {
                Method m = (Method) o;
                m.invoke(modInstance);
            }
        }
        catch (Throwable t)
        {
            controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
        }
    }
}
