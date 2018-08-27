/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.Mod.Metadata;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLFingerprintViolationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.DependencyParser;
import net.minecraftforge.fml.common.versioning.VersionParser;
import net.minecraftforge.fml.common.versioning.VersionRange;
import net.minecraftforge.fml.relauncher.Side;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Level;

import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import java.util.function.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.apache.logging.log4j.message.FormattedMessage;

import javax.annotation.Nullable;

public class FMLModContainer implements ModContainer
{
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
    private DefaultArtifactVersion processedVersion;

    private String annotationDependencies;
    private VersionRange minecraftAccepted;
    private boolean fingerprintNotPresent;
    private Set<String> sourceFingerprints;
    private Certificate certificate;
    private String modLanguage;
    private ILanguageAdapter languageAdapter;
    private Disableable disableability;
    private ListMultimap<Class<? extends FMLEvent>, Method> eventMethods;
    private Map<String, String> customModProperties;
    private ModCandidate candidate;
    private URL updateJSONUrl;
    private int classVersion;

    public FMLModContainer(String className, ModCandidate container, Map<String, Object> modDescriptor)
    {
        this.className = className;
        this.source = container.getModContainer();
        this.candidate = container;
        this.descriptor = modDescriptor;
        this.eventMethods = ArrayListMultimap.create();

        this.modLanguage = (String)modDescriptor.get("modLanguage");
        String languageAdapterType = (String)modDescriptor.get("modLanguageAdapter");
        if (Strings.isNullOrEmpty(languageAdapterType))
        {
            this.languageAdapter = "scala".equals(modLanguage) ? new ILanguageAdapter.ScalaAdapter() : new ILanguageAdapter.JavaAdapter();
        }
        else
        {
            // Delay loading of the adapter until the mod is on the classpath, in case the mod itself contains it.
            this.languageAdapter = null;
            FMLLog.log.trace("Using custom language adapter {} for {} (modid: {})", languageAdapterType, this.className, getModId());
        }
        sanityCheckModId();
    }

    private void sanityCheckModId()
    {
        String modid = (String)this.descriptor.get("modid");
        if (Strings.isNullOrEmpty(modid))
        {
            throw new IllegalArgumentException("The modId is null or empty");
        }
        if (modid.length() > 64)
        {
            throw new IllegalArgumentException(String.format("The modId %s is longer than the maximum of 64 characters.", modid));
        }
        if (!modid.equals(modid.toLowerCase(Locale.ENGLISH)))
        {
            throw new IllegalArgumentException(String.format("The modId %s must be all lowercase.", modid));
        }
    }

    private ILanguageAdapter getLanguageAdapter()
    {
        if (languageAdapter == null)
        {
            try
            {
                languageAdapter = (ILanguageAdapter)Class.forName((String)descriptor.get("modLanguageAdapter"), true, Loader.instance().getModClassLoader()).newInstance();
            }
            catch (Exception ex)
            {
                FMLLog.log.error("Error constructing custom mod language adapter referenced by {} (modid: {})", getModId(), ex);
                throw new RuntimeException(ex);
            }
        }
        return languageAdapter;
    }

    @Override
    public String getModId()
    {
        return (String)descriptor.get("modid");
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
            overridesMetadata = !((Boolean)descriptor.get("useMetadata"));
        }

        if (overridesMetadata || !modMetadata.useDependencyInformation)
        {
            annotationDependencies = (String)descriptor.get("dependencies");
            DependencyParser dependencyParser = new DependencyParser(getModId(), FMLCommonHandler.instance().getSide());
            DependencyParser.DependencyInfo info = dependencyParser.parseDependencies(annotationDependencies);
            info.dependants.addAll(Loader.instance().getInjectedBefore(getModId()));
            info.dependencies.addAll(Loader.instance().getInjectedAfter(getModId()));
            modMetadata.requiredMods = info.requirements;
            modMetadata.dependencies = info.dependencies;
            modMetadata.dependants = info.dependants;
            FMLLog.log.trace("Parsed dependency info for {}: Requirements: {} After:{} Before:{}", getModId(), info.requirements, info.dependencies, info.dependants);
        }
        else
        {
            FMLLog.log.trace("Using mcmod dependency info for {}: {} {} {}", getModId(), modMetadata.requiredMods, modMetadata.dependencies, modMetadata.dependants);
        }
        if (Strings.isNullOrEmpty(modMetadata.name))
        {
            FMLLog.log.info("Mod {} is missing the required element 'name'. Substituting {}", getModId(), getModId());
            modMetadata.name = getModId();
        }
        internalVersion = (String)descriptor.get("version");
        if (Strings.isNullOrEmpty(internalVersion))
        {
            Properties versionProps = searchForVersionProperties();
            if (versionProps != null)
            {
                internalVersion = versionProps.getProperty(getModId() + ".version");
                FMLLog.log.debug("Found version {} for mod {} in version.properties, using", internalVersion, getModId());
            }

        }
        if (Strings.isNullOrEmpty(internalVersion) && !Strings.isNullOrEmpty(modMetadata.version))
        {
            FMLLog.log.warn("Mod {} is missing the required element 'version' and a version.properties file could not be found. Falling back to metadata version {}", getModId(), modMetadata.version);
            internalVersion = modMetadata.version;
        }
        if (Strings.isNullOrEmpty(internalVersion))
        {
            FMLLog.log.warn("Mod {} is missing the required element 'version' and no fallback can be found. Substituting '1.0'.", getModId());
            modMetadata.version = internalVersion = "1.0";
        }

        String mcVersionString = (String)descriptor.get("acceptedMinecraftVersions");
        if ("[1.12]".equals(mcVersionString))
            mcVersionString = "[1.12,1.12.2]";
        if ("[1.12.1]".equals(mcVersionString) || "[1.12,1.12.1]".equals(mcVersionString))
            mcVersionString = "[1.12,1.12.2]";

        if (!Strings.isNullOrEmpty(mcVersionString))
        {
            minecraftAccepted = VersionParser.parseRange(mcVersionString);
        }
        else
        {
            minecraftAccepted = Loader.instance().getMinecraftModContainer().getStaticVersionRange();
        }

        String jsonURL = (String)descriptor.get("updateJSON");
        if (!Strings.isNullOrEmpty(jsonURL))
        {
            try
            {
                this.updateJSONUrl = new URL(jsonURL);
            }
            catch (MalformedURLException e)
            {
                FMLLog.log.debug("Specified json URL for mod '{}' is invalid: {}", getModId(), jsonURL);
            }
        }
    }

    @Nullable
    public Properties searchForVersionProperties()
    {
        try
        {
            FMLLog.log.debug("Attempting to load the file version.properties from {} to locate a version number for mod {}", getSource().getName(), getModId());
            Properties version = null;
            if (getSource().isFile())
            {
                ZipFile source = new ZipFile(getSource());
                ZipEntry versionFile = source.getEntry("version.properties");
                if (versionFile != null)
                {
                    version = new Properties();
                    InputStream sourceInputStream = source.getInputStream(versionFile);
                    try
                    {
                        version.load(sourceInputStream);
                    }
                    finally
                    {
                        IOUtils.closeQuietly(sourceInputStream);
                    }
                }
                source.close();
            }
            else if (getSource().isDirectory())
            {
                File propsFile = new File(getSource(), "version.properties");
                if (propsFile.exists() && propsFile.isFile())
                {
                    version = new Properties();
                    try (FileInputStream fis = new FileInputStream(propsFile))
                    {
                        version.load(fis);
                    }
                }
            }
            return version;
        }
        catch (IOException e)
        {
            FMLLog.log.trace("Failed to find a usable version.properties file for mod {}", getModId());
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
            FMLLog.log.debug("Enabling mod {}", getModId());
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

    @Nullable
    private Method gatherAnnotations(Class<?> clazz)
    {
        Method factoryMethod = null;
        for (Method m : clazz.getDeclaredMethods())
        {
            for (Annotation a : m.getAnnotations())
            {
                if (a.annotationType().equals(Mod.EventHandler.class))
                {
                    if (m.getParameterTypes().length == 1 && FMLEvent.class.isAssignableFrom(m.getParameterTypes()[0]))
                    {
                        m.setAccessible(true);
                        @SuppressWarnings("unchecked")
                        Class<? extends FMLEvent> parameterType = (Class<? extends FMLEvent>) m.getParameterTypes()[0];
                        eventMethods.put(parameterType, m);
                    }
                    else
                    {
                        FMLLog.log.error("The mod {} appears to have an invalid event annotation {}. This annotation can only apply to methods with recognized event arguments - it will not be called", getModId(), a.annotationType().getSimpleName());
                    }
                }
                else if (a.annotationType().equals(Mod.InstanceFactory.class))
                {
                    if (Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0 && factoryMethod == null)
                    {
                        m.setAccessible(true);
                        factoryMethod = m;
                    }
                    else if (!(Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0))
                    {
                        FMLLog.log.error("The InstanceFactory annotation can only apply to a static method, taking zero arguments - it will be ignored on {}({}) for mod {}", m.getName(), Arrays.asList(m.getParameterTypes()), getModId());
                    }
                    else if (factoryMethod != null)
                    {
                        FMLLog.log.error("The InstanceFactory annotation can only be used once, the application to {}({}) will be ignored for mod {}", m.getName(), Arrays.asList(m.getParameterTypes()), getModId());
                    }
                }
            }
        }
        return factoryMethod;
    }

    private void processFieldAnnotations(ASMDataTable asmDataTable) throws IllegalAccessException
    {
        SetMultimap<String, ASMData> annotations = asmDataTable.getAnnotationsFor(this);

        parseSimpleFieldAnnotation(annotations, Instance.class.getName(), ModContainer::getMod);
        parseSimpleFieldAnnotation(annotations, Metadata.class.getName(), ModContainer::getMetadata);
    }

    private void parseSimpleFieldAnnotation(SetMultimap<String, ASMData> annotations, String annotationClassName, Function<ModContainer, Object> retriever) throws IllegalAccessException
    {
        Set<ASMDataTable.ASMData> mods = annotations.get(Mod.class.getName());
        String[] annName = annotationClassName.split("\\.");
        String annotationName = annName[annName.length - 1];
        for (ASMData targets : annotations.get(annotationClassName))
        {
            String targetMod = (String)targets.getAnnotationInfo().get("value");
            String owner = (String)targets.getAnnotationInfo().get("owner");
            if (Strings.isNullOrEmpty(owner))
            {
                owner = ASMDataTable.getOwnerModID(mods, targets);
                if (Strings.isNullOrEmpty(owner))
                {
                    FMLLog.bigWarning("Could not determine owning mod for @{} on {} for mod {}", annotationClassName, targets.getClassName(), this.getModId());
                    continue;
                }
            }
            if (!this.getModId().equals(owner))
            {
                FMLLog.log.debug("Skipping @{} injection for {}.{} since it is not for mod {}", annotationClassName, targets.getClassName(), targets.getObjectName(), this.getModId());
                continue;
            }
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
                    injectedMod = retriever.apply(mc);
                }
                catch (ReflectiveOperationException e)
                {
                    FMLLog.log.warn("Attempting to load @{} in class {} for {} and failing", annotationName, targets.getClassName(), mc.getModId(), e);
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
                        FMLLog.log.warn("Unable to inject @{} in non-static field {}.{} for {} as it is NOT the primary mod instance", annotationName, targets.getClassName(), targets.getObjectName(), mc.getModId());
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
        ModClassLoader modClassLoader = event.getModClassLoader();
        try
        {
            modClassLoader.addFile(source);
        }
        catch (MalformedURLException e)
        {
            FormattedMessage message = new FormattedMessage("{} Failed to add file to classloader: {}", getModId(), source);
            throw new LoaderException(message.getFormattedMessage(), e);
        }
        modClassLoader.clearNegativeCacheFor(candidate.getClassList());

        //Only place I could think to add this...
        MinecraftForge.preloadCrashClasses(event.getASMHarvestedData(), getModId(), candidate.getClassList());

        Class<?> clazz;
        try
        {
            clazz = Class.forName(className, true, modClassLoader);
        }
        catch (ClassNotFoundException e)
        {
            FormattedMessage message = new FormattedMessage("{} Failed load class: {}", getModId(), className);
            throw new LoaderException(message.getFormattedMessage(), e);
        }

        Certificate[] certificates = clazz.getProtectionDomain().getCodeSource().getCertificates();
        ImmutableList<String> certList = CertificateHelper.getFingerprints(certificates);
        sourceFingerprints = ImmutableSet.copyOf(certList);

        String expectedFingerprint = (String)descriptor.get("certificateFingerprint");

        fingerprintNotPresent = true;

        if (expectedFingerprint != null && !expectedFingerprint.isEmpty())
        {
            if (!sourceFingerprints.contains(expectedFingerprint))
            {
                Level warnLevel = source.isDirectory() ? Level.TRACE : Level.ERROR;
                FMLLog.log.log(warnLevel, "The mod {} is expecting signature {} for source {}, however there is no signature matching that description", getModId(), expectedFingerprint, source.getName());
            }
            else
            {
                certificate = certificates[certList.indexOf(expectedFingerprint)];
                fingerprintNotPresent = false;
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, String>> props = (List<Map<String, String>>)descriptor.get("customProperties");
        if (props != null)
        {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            for (Map<String, String> p : props)
            {
                builder.put(p.get("k"), p.get("v"));
            }
            customModProperties = builder.build();
        }
        else
        {
            customModProperties = EMPTY_PROPERTIES;
        }

        Boolean hasDisableableFlag = (Boolean)descriptor.get("canBeDeactivated");
        boolean hasReverseDepends = !event.getReverseDependencies().get(getModId()).isEmpty();
        if (hasDisableableFlag != null && hasDisableableFlag)
        {
            disableability = hasReverseDepends ? Disableable.DEPENDENCIES : Disableable.YES;
        }
        else
        {
            disableability = hasReverseDepends ? Disableable.DEPENDENCIES : Disableable.RESTART;
        }
        Method factoryMethod = gatherAnnotations(clazz);
        ILanguageAdapter languageAdapter = getLanguageAdapter();
        try
        {
            modInstance = languageAdapter.getNewInstance(this, clazz, modClassLoader, factoryMethod);
        }
        catch (Exception e)
        {
            FormattedMessage message = new FormattedMessage("{} Failed to load new mod instance.", getModId());
            throw new LoaderException(message.getFormattedMessage(), e);
        }
        NetworkRegistry.INSTANCE.register(this, clazz, (String)(descriptor.getOrDefault("acceptableRemoteVersions", null)), event.getASMHarvestedData());
        if (fingerprintNotPresent)
        {
            eventBus.post(new FMLFingerprintViolationEvent(source.isDirectory(), source, ImmutableSet.copyOf(this.sourceFingerprints), expectedFingerprint));
        }
        ProxyInjector.inject(this, event.getASMHarvestedData(), FMLCommonHandler.instance().getSide(), languageAdapter);
        AutomaticEventSubscriber.inject(this, event.getASMHarvestedData(), FMLCommonHandler.instance().getSide());
        ConfigManager.sync(this.getModId(), Config.Type.INSTANCE);

        try
        {
            processFieldAnnotations(event.getASMHarvestedData());
        }
        catch (IllegalAccessException e)
        {
            FormattedMessage message = new FormattedMessage("{} Failed to process field annotations.", getModId());
            throw new LoaderException(message.getFormattedMessage(), e);
        }
    }

    @Subscribe
    public void handleModStateEvent(FMLEvent event)
    {
        if (!eventMethods.containsKey(event.getClass()))
        {
            return;
        }
        try
        {
            for (Method m : eventMethods.get(event.getClass()))
            {
                m.invoke(modInstance, event);
            }
        }
        catch (Throwable t)
        {
            controller.errorOccurred(this, t);
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

    @Override
    public String toString()
    {
        return "FMLMod:" + getModId() + "{" + getVersion() + "}";
    }

    @Override
    public Map<String, String> getCustomModProperties()
    {
        return customModProperties;
    }

    @Override
    public Class<?> getCustomResourcePackClass()
    {
        try
        {
            return getSource().isDirectory() ? Class.forName("net.minecraftforge.fml.client.FMLFolderResourcePack", true, getClass().getClassLoader()) : Class.forName("net.minecraftforge.fml.client.FMLFileResourcePack", true, getClass().getClassLoader());
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }

    @Override
    public Map<String, String> getSharedModDescriptor()
    {
        Map<String, String> descriptor = Maps.newHashMap();
        descriptor.put("modsystem", "FML");
        descriptor.put("id", getModId());
        descriptor.put("version", getDisplayVersion());
        descriptor.put("name", getName());
        descriptor.put("url", modMetadata.url);
        descriptor.put("authors", modMetadata.getAuthorList());
        descriptor.put("description", modMetadata.description);
        return descriptor;
    }

    @Override
    public Disableable canBeDisabled()
    {
        return disableability;
    }

    @Override
    public String getGuiClassName()
    {
        return (String)descriptor.get("guiFactory");
    }

    @Override
    public List<String> getOwnedPackages()
    {
        return candidate.getContainedPackages();
    }

    private boolean isTrue(Boolean value)
    {
        if (value == null)
        {
            return false;
        }
        return value;
    }

    @Override
    public boolean shouldLoadInEnvironment()
    {
        boolean clientSideOnly = isTrue((Boolean)descriptor.get("clientSideOnly"));
        boolean serverSideOnly = isTrue((Boolean)descriptor.get("serverSideOnly"));

        if (clientSideOnly && serverSideOnly)
        {
            throw new RuntimeException("Mod annotation claims to be both client and server side only!");
        }

        Side side = FMLCommonHandler.instance().getSide();

        if (clientSideOnly && side != Side.CLIENT)
        {
            FMLLog.log.info("Disabling mod {} it is client side only.", getModId());
            return false;
        }

        if (serverSideOnly && side != Side.SERVER)
        {
            FMLLog.log.info("Disabling mod {} it is server side only.", getModId());
            return false;
        }

        return true;
    }

    @Override
    public URL getUpdateUrl()
    {
        return updateJSONUrl;
    }

    @Override
    public void setClassVersion(int classVersion)
    {
        this.classVersion = classVersion;
    }

    @Override
    public int getClassVersion()
    {
        return this.classVersion;
    }
}
