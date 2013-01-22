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
import java.io.FileInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.Metadata;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ASMDataTable.ASMData;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms.IMCEvent;
import cpw.mods.fml.common.event.FMLFingerprintViolationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.event.FMLServerStoppedEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.event.FMLStateEvent;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public class FMLModContainer implements ModContainer
{
    private Mod modDescriptor;
    private Object modInstance;
    private File source;
    private ModMetadata modMetadata;
    private String className;
    private Map<String, Object> descriptor;
    private boolean enabled = true;
    private String internalVersion;
    private boolean overridesMetadata;
    private EventBus eventBus;
    private LoadController controller;
    private Multimap<Class<? extends Annotation>, Object> annotations;
    private DefaultArtifactVersion processedVersion;
    private boolean isNetworkMod;

    private static final BiMap<Class<? extends FMLEvent>, Class<? extends Annotation>> modAnnotationTypes = ImmutableBiMap.<Class<? extends FMLEvent>, Class<? extends Annotation>>builder()
        .put(FMLPreInitializationEvent.class, Mod.PreInit.class)
        .put(FMLInitializationEvent.class, Mod.Init.class)
        .put(FMLPostInitializationEvent.class, Mod.PostInit.class)
        .put(FMLServerAboutToStartEvent.class, Mod.ServerAboutToStart.class)
        .put(FMLServerStartingEvent.class, Mod.ServerStarting.class)
        .put(FMLServerStartedEvent.class, Mod.ServerStarted.class)
        .put(FMLServerStoppingEvent.class, Mod.ServerStopping.class)
        .put(FMLServerStoppedEvent.class, Mod.ServerStopped.class)
        .put(IMCEvent.class,Mod.IMCCallback.class)
        .put(FMLFingerprintViolationEvent.class, Mod.FingerprintWarning.class)
        .build();
    private static final BiMap<Class<? extends Annotation>, Class<? extends FMLEvent>> modTypeAnnotations = modAnnotationTypes.inverse();
    private String annotationDependencies;
    private VersionRange minecraftAccepted;
    private boolean fingerprintNotPresent;
    private Set<String> sourceFingerprints;
    private Certificate certificate;


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
        return internalVersion;
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

        if (descriptor.containsKey("useMetadata"))
        {
            overridesMetadata = !((Boolean)descriptor.get("useMetadata")).booleanValue();
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
        else
        {
            FMLLog.finest("Using mcmod dependency info : %s %s %s", modMetadata.requiredMods, modMetadata.dependencies, modMetadata.dependants);
        }
        if (Strings.isNullOrEmpty(modMetadata.name))
        {
            FMLLog.info("Mod %s is missing the required element 'name'. Substituting %s", getModId(), getModId());
            modMetadata.name = getModId();
        }
        internalVersion = (String) descriptor.get("version");
        if (Strings.isNullOrEmpty(internalVersion))
        {
            Properties versionProps = searchForVersionProperties();
            if (versionProps != null)
            {
                internalVersion = versionProps.getProperty(getModId()+".version");
                FMLLog.fine("Found version %s for mod %s in version.properties, using", internalVersion, getModId());
            }

        }
        if (Strings.isNullOrEmpty(internalVersion) && !Strings.isNullOrEmpty(modMetadata.version))
        {
            FMLLog.warning("Mod %s is missing the required element 'version' and a version.properties file could not be found. Falling back to metadata version %s", getModId(), modMetadata.version);
            internalVersion = modMetadata.version;
        }
        if (Strings.isNullOrEmpty(internalVersion))
        {
            FMLLog.warning("Mod %s is missing the required element 'version' and no fallback can be found. Substituting '1.0'.", getModId());
            modMetadata.version = internalVersion = "1.0";
        }

        String mcVersionString = (String) descriptor.get("acceptedMinecraftVersions");
        if (!Strings.isNullOrEmpty(mcVersionString))
        {
            minecraftAccepted = VersionParser.parseRange(mcVersionString);
        }
        else
        {
            minecraftAccepted = Loader.instance().getMinecraftModContainer().getStaticVersionRange();
        }
    }

    public Properties searchForVersionProperties()
    {
        try
        {
            FMLLog.fine("Attempting to load the file version.properties from %s to locate a version number for %s", getSource().getName(), getModId());
            Properties version = null;
            if (getSource().isFile())
            {
                ZipFile source = new ZipFile(getSource());
                ZipEntry versionFile = source.getEntry("version.properties");
                if (versionFile!=null)
                {
                    version = new Properties();
                    version.load(source.getInputStream(versionFile));
                }
                source.close();
            }
            else if (getSource().isDirectory())
            {
                File propsFile = new File(getSource(),"version.properties");
                if (propsFile.exists() && propsFile.isFile())
                {
                    version = new Properties();
                    FileInputStream fis = new FileInputStream(propsFile);
                    version.load(fis);
                    fis.close();
                }
            }
            return version;
        }
        catch (Exception e)
        {
            Throwables.propagateIfPossible(e);
            FMLLog.fine("Failed to find a usable version.properties file");
            return null;
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
        return ((overridesMetadata || !modMetadata.useDependencyInformation) ? Strings.nullToEmpty(annotationDependencies) : modMetadata.printableSortingRules());
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

    private void processFieldAnnotations(ASMDataTable asmDataTable) throws Exception
    {
        SetMultimap<String, ASMData> annotations = asmDataTable.getAnnotationsFor(this);

        parseSimpleFieldAnnotation(annotations, Instance.class.getName(), new Function<ModContainer, Object>()
        {
            public Object apply(ModContainer mc)
            {
                return mc.getMod();
            }
        });
        parseSimpleFieldAnnotation(annotations, Metadata.class.getName(), new Function<ModContainer, Object>()
        {
            public Object apply(ModContainer mc)
            {
                return mc.getMetadata();
            }
        });
    }

    private void parseSimpleFieldAnnotation(SetMultimap<String, ASMData> annotations, String annotationClassName, Function<ModContainer, Object> retreiver) throws IllegalAccessException
    {
        String[] annName = annotationClassName.split("\\.");
        String annotationName = annName[annName.length - 1];
        for (ASMData targets : annotations.get(annotationClassName))
        {
            String targetMod = (String) targets.getAnnotationInfo().get("value");
            Field f = null;
            Object injectedMod = null;
            ModContainer mc = this;
            boolean isStatic = false;
            Class<?> clz = modInstance.getClass();
            if (!Strings.isNullOrEmpty(targetMod))
            {
                if (Loader.isModLoaded(targetMod))
                {
                    mc = Loader.instance().getIndexedModList().get(targetMod);
                }
                else
                {
                    mc = null;
                }
            }
            if (mc != null)
            {
                try
                {
                    clz = Class.forName(targets.getClassName(), true, Loader.instance().getModClassLoader());
                    f = clz.getDeclaredField(targets.getObjectName());
                    f.setAccessible(true);
                    isStatic = Modifier.isStatic(f.getModifiers());
                    injectedMod = retreiver.apply(mc);
                }
                catch (Exception e)
                {
                    Throwables.propagateIfPossible(e);
                    FMLLog.log(Level.WARNING, e, "Attempting to load @%s in class %s for %s and failing", annotationName, targets.getClassName(), mc.getModId());
                }
            }
            if (f != null)
            {
                Object target = null;
                if (!isStatic)
                {
                    target = modInstance;
                    if (!modInstance.getClass().equals(clz))
                    {
                        FMLLog.warning("Unable to inject @%s in non-static field %s.%s for %s as it is NOT the primary mod instance", annotationName, targets.getClassName(), targets.getObjectName(), mc.getModId());
                        continue;
                    }
                }
                f.set(target, injectedMod);
            }
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

            Certificate[] certificates = clazz.getProtectionDomain().getCodeSource().getCertificates();
            int len = 0;
            if (certificates != null)
            {
                len = certificates.length;
            }
            Builder<String> certBuilder = ImmutableList.<String>builder();
            for (int i = 0; i < len; i++)
            {
                certBuilder.add(CertificateHelper.getFingerprint(certificates[i]));
            }

            ImmutableList<String> certList = certBuilder.build();
            sourceFingerprints = ImmutableSet.copyOf(certList);

            String expectedFingerprint = (String) descriptor.get("certificateFingerprint");

            fingerprintNotPresent = true;

            if (expectedFingerprint != null && !expectedFingerprint.isEmpty())
            {
                if (!sourceFingerprints.contains(expectedFingerprint))
                {
                    Level warnLevel = Level.SEVERE;
                    if (source.isDirectory())
                    {
                        warnLevel = Level.FINER;
                    }
                    FMLLog.log(warnLevel, "The mod %s is expecting signature %s for source %s, however there is no signature matching that description", getModId(), expectedFingerprint, source.getName());
                }
                else
                {
                    certificate = certificates[certList.indexOf(expectedFingerprint)];
                    fingerprintNotPresent = false;
                }
            }

            annotations = gatherAnnotations(clazz);
            isNetworkMod = FMLNetworkHandler.instance().registerNetworkMod(this, clazz, event.getASMHarvestedData());
            modInstance = clazz.newInstance();
            if (fingerprintNotPresent)
            {
                eventBus.post(new FMLFingerprintViolationEvent(source.isDirectory(), source, ImmutableSet.copyOf(this.sourceFingerprints), expectedFingerprint));
            }
            ProxyInjector.inject(this, event.getASMHarvestedData(), FMLCommonHandler.instance().getSide());
            processFieldAnnotations(event.getASMHarvestedData());
        }
        catch (Throwable e)
        {
            controller.errorOccurred(this, e);
            Throwables.propagateIfPossible(e);
        }
    }

    @Subscribe
    public void handleModStateEvent(FMLEvent event)
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

    @Override
    public String getDisplayVersion()
    {
        return modMetadata.version;
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange()
    {
        return minecraftAccepted;
    }

    @Override
    public Certificate getSigningCertificate()
    {
        return certificate;
    }
}
