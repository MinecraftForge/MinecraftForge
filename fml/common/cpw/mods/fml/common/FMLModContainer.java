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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;

public class FMLModContainer implements ModContainer
{
    private Mod modDescriptor;
    private Object modInstance;
    private File source;
    private ModMetadata modMetadata;
    private String className;
    private Map<String, Object> descriptor;
    private boolean enabled = true;
    private List<ArtifactVersion> requirements;
    private List<ArtifactVersion> dependencies;
    private List<ArtifactVersion> dependants;
    private boolean overridesMetadata;
    private EventBus eventBus;
    private LoadController controller;
    private Multimap<Class<? extends Annotation>, Object> annotations;
    private DefaultArtifactVersion processedVersion;
    private boolean isNetworkMod;

    private static final BiMap<Class<? extends FMLStateEvent>, Class<? extends Annotation>> modAnnotationTypes = ImmutableBiMap.<Class<? extends FMLStateEvent>, Class<? extends Annotation>>builder()
        .put(FMLPreInitializationEvent.class, Mod.PreInit.class)
        .put(FMLInitializationEvent.class, Mod.Init.class)
        .put(FMLPostInitializationEvent.class, Mod.PostInit.class)
        .put(FMLServerStartingEvent.class, Mod.ServerStarting.class)
        .put(FMLServerStartedEvent.class, Mod.ServerStarted.class)
        .put(FMLServerStoppingEvent.class, Mod.ServerStopping.class)
        .build();
    private static final BiMap<Class<? extends Annotation>, Class<? extends FMLStateEvent>> modTypeAnnotations = modAnnotationTypes.inverse();
    private String annotationDependencies;


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
            Set<ArtifactVersion> requirements = Sets.newHashSet();
            List<ArtifactVersion> dependencies = Lists.newArrayList();
            List<ArtifactVersion> dependants = Lists.newArrayList();
            annotationDependencies = (String) descriptor.get("dependencies");
            Loader.instance().computeDependencies(annotationDependencies, requirements, dependencies, dependants);
            modMetadata.requiredMods = requirements;
            modMetadata.dependencies = dependencies;
            modMetadata.dependants = dependants;
            FMLLog.finest("Parsed dependency info : %s %s %s", requirements, dependencies, dependants);
        }
        if (Strings.isNullOrEmpty(modMetadata.name))
        {
            FMLLog.info("Mod %s is missing a required element, name. Substituting %s", getModId(), getModId());
            modMetadata.name = getModId();
        }
        if (Strings.isNullOrEmpty(modMetadata.version))
        {
            FMLLog.warning("Mod %s is missing a required element, version. Substituting 1", getModId());
            modMetadata.version = "1";
        }
    }

    @Override
    public void setEnabledState(boolean enabled)
    {
        this.enabled = enabled;
    }

    @Override
    public Set<ArtifactVersion> getRequirements()
    {
        return modMetadata.requiredMods;
    }

    @Override
    public List<ArtifactVersion> getDependencies()
    {
        return modMetadata.dependencies;
    }

    @Override
    public List<ArtifactVersion> getDependants()
    {
        return modMetadata.dependants;
    }

    @Override
    public String getSortingRules()
    {
        return ((overridesMetadata || !modMetadata.useDependencyInformation) ? annotationDependencies : modMetadata.printableSortingRules());
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
                if (modTypeAnnotations.containsKey(a.annotationType()))
                {
                    Class<?>[] paramTypes = new Class[] { modTypeAnnotations.get(a.annotationType()) };

                    if (Arrays.equals(m.getParameterTypes(), paramTypes))
                    {
                        m.setAccessible(true);
                        anns.put(a.annotationType(), m);
                    }
                    else
                    {
                        FMLLog.severe("The mod %s appears to have an invalid method annotation %s. This annotation can only apply to methods with argument types %s -it will not be called", getModId(), a.annotationType().getSimpleName(), Arrays.toString(paramTypes));
                    }
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
//TODO
//        for (Object o : annotations.get(Block.class))
//        {
//            Field f = (Field) o;
//            f.set(modInstance, GameRegistry.buildBlock(this, f.getType(), f.getAnnotation(Block.class)));
//        }
    }

    @Subscribe
    public void constructMod(FMLConstructionEvent event)
    {
        try
        {
            ModClassLoader modClassLoader = event.getModClassLoader();
            modClassLoader.addFile(source);
            Class<?> clazz = Class.forName(className, true, modClassLoader);
            ASMDataTable asmHarvestedAnnotations = event.getASMHarvestedData();
            // TODO
            asmHarvestedAnnotations.getAnnotationsFor(this);
            annotations = gatherAnnotations(clazz);
            isNetworkMod = FMLNetworkHandler.instance().registerNetworkMod(this, clazz, event.getASMHarvestedData());
            modInstance = clazz.newInstance();
            ProxyInjector.inject(this, event.getASMHarvestedData(), FMLCommonHandler.instance().getSide());
            processFieldAnnotations();
        }
        catch (Throwable e)
        {
            controller.errorOccurred(this, e);
            Throwables.propagateIfPossible(e);
        }
    }

    @Subscribe
    public void handleModStateEvent(FMLStateEvent event)
    {
        Class<? extends Annotation> annotation = modAnnotationTypes.get(event.getClass());
        if (annotation == null)
        {
            return;
        }
        try
        {
            for (Object o : annotations.get(annotation))
            {
                Method m = (Method) o;
                m.invoke(modInstance, event);
            }
        }
        catch (Throwable t)
        {
            controller.errorOccurred(this, t);
            Throwables.propagateIfPossible(t);
        }
    }

    @Override
    public ArtifactVersion getProcessedVersion()
    {
        if (processedVersion == null)
        {
            processedVersion = new DefaultArtifactVersion(getModId(), getVersion());
        }
        return processedVersion;
    }
    @Override
    public boolean isImmutable()
    {
        return false;
    }

    @Override
    public boolean isNetworkMod()
    {
        return isNetworkMod;
    }
}
