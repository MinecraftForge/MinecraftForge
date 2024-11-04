/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.javafmlmod;

import net.minecraftforge.eventbus.EventBusErrorMessage;
import net.minecraftforge.eventbus.api.BusBuilder;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.IEventListener;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingException;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import net.minecraftforge.unsafe.UnsafeHacks;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import cpw.mods.jarhandling.SecureJar;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;

public class FMLModContainer extends ModContainer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker LOADING = MarkerManager.getMarker("LOADING");
    private final ModFileScanData scanResults;
    private final IEventBus eventBus;
    private Object modInstance;
    private final Class<?> modClass;
    private final FMLJavaModLoadingContext context = new FMLJavaModLoadingContext(this);

    public FMLModContainer(IModInfo info, String className, ModFileScanData modFileScanResults, ModuleLayer gameLayer) {
        super(info);
        LOGGER.debug(LOADING,"Creating FMLModContainer instance for {}", className);
        this.scanResults = modFileScanResults;
        activityMap.put(ModLoadingStage.CONSTRUCT, this::constructMod);
        this.eventBus = BusBuilder.builder().setExceptionHandler(FMLModContainer::onEventFailed).setTrackPhases(false).markerType(IModBusEvent.class).useModLauncher().build();
        this.configHandler = Optional.of(ce->this.eventBus.post(ce.self()));
        this.contextExtension = () -> context;
        try {
            var moduleName = info.getOwningFile().moduleName();
            var module = gameLayer.findModule(moduleName)
                .orElseThrow(() -> new IllegalStateException("Failed to find " + moduleName + " in " + gameLayer));

            openModules(gameLayer, module, info.getOwningFile().getFile().getSecureJar());

            modClass = Class.forName(module, className);
            LOGGER.debug(LOADING,"Loaded modclass {}/{} with {}", modClass.getModule().getName(), modClass.getName(), modClass.getClassLoader());
        } catch (Throwable e) {
            LOGGER.error(LOADING, "Failed to load class {}", className, e);
            throw new ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e);
        }
    }

    /**
     * <pre>
     * Reads the Add-Exports and Add-Opens attributes (see note) from a mod file and
     * attempts to apply them. This differers from the JEP in two significant ways:
     *   1) Instead of opening things to ALL-UNNAMED it instead only opens to the
     *      mod's module itself. Because I don't want other mods accidently relying
     *      on transitive behavior
     *   2) It is read from all mods not just the executable jars.
     *
     * From <a href = "https://openjdk.org/jeps/261">JEP 261: Module System</a>
     *
     * Two new JDK-specific JAR-file manifest attributes are defined to correspond
     * to the --add-exports and --add-opens command-line options:
     *     Add-Exports: &lt;module&gt;/&lt;package&gt;( &lt;module&gt;/&lt;package&gt;)*
     *     Add-Opens: &lt;module&gt;/&lt;package&gt;( &lt;module&gt;/&lt;package&gt;)*
     *
     * The value of each attribute is a space-separated list of slash-separated
     * module-name/package-name pairs. A &gt;module&lt;/&gt;package&lt; pair in
     * the value of an Add-Exports attribute has the same meaning as the
     * command-line option --add-exports &gt;module&lt;/&gt;package&lt;=ALL-UNNAMED.
     *
     * A &gt;module&lt;/&gt;package&lt; pair in the value of an Add-Opens attribute has the
     * same meaning as the command-line option --add-opens &gt;module&lt;/&gt;package&lt;=ALL-UNNAMED.
     *
     * Each attribute can occur at most once, in the main section of a MANIFEST.MF file.
     * A particular pair can be listed more than once. If a specified module was not
     * resolved, or if a specified package does not exist, then the corresponding pair
     * is ignored.
     */
    private static void openModules(ModuleLayer layer, Module self, SecureJar jar) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        var manifest = jar.moduleDataProvider().getManifest().getMainAttributes();
        addOpenOrExports(layer, self, true, manifest);
        addOpenOrExports(layer, self, false, manifest);
    }

    private static void addOpenOrExports(ModuleLayer layer, Module self, boolean open, Attributes attrs) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        var key = open ? "Add-Opens" : "Add-Exports";
        var entry = attrs.getValue(key);
        if (entry == null)
            return;

        for (var pair : entry.split(" ")) {
            var pts = pair.trim().split("/");
            if (pts.length == 2) {
                var target = layer.findModule(pts[0]).orElse(null);
                if (target == null || !target.getDescriptor().packages().contains(pts[1]))
                    continue;
                addOpenOrExport(target, pts[1], self, open);
            } else {
                LOGGER.warn(LOADING, "Invalid {} entry in {}: {}", key, self.getName(), pair);
            }
        }
    }

    private static Method implAddExportsOrOpens;
    private static void addOpenOrExport(Module target, String pkg, Module reader, boolean open) throws NoSuchMethodException, SecurityException, IllegalAccessException, InvocationTargetException {
        if (implAddExportsOrOpens == null) {
            implAddExportsOrOpens = Module.class.getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class, boolean.class);
            UnsafeHacks.setAccessible(implAddExportsOrOpens);
        }

        LOGGER.info(LOADING, "{} {}/{} to {}", open ? "Opening" : "Exporting", target.getName(), pkg, reader.getName());
        implAddExportsOrOpens.invoke(target, pkg, reader, open, /*syncVM*/true);
    }

    private static void onEventFailed(IEventBus iEventBus, Event event, IEventListener[] iEventListeners, int i, Throwable throwable) {
        LOGGER.error(new EventBusErrorMessage(event, i, iEventListeners, throwable));
    }

    private void constructMod() {
        try {
            LOGGER.trace(LOADING, "Loading mod instance {} of type {}", getModId(), modClass.getName());
            Constructor<?> constructor;
            try {
                constructor = modClass.getDeclaredConstructor(context.getClass());
            } catch (NoSuchMethodException | SecurityException exception) {
                constructor = modClass.getDeclaredConstructor();
            }
            this.modInstance = constructor.getParameterCount() == 0 ? constructor.newInstance() : constructor.newInstance(context);
            LOGGER.trace(LOADING, "Loaded mod instance {} of type {}", getModId(), modClass.getName());
        } catch (Throwable e) {
            // When a mod constructor throws an exception, it's wrapped in an InvocationTargetException which hides the
            // actual exception from the mod loading error screen.
            if (e instanceof InvocationTargetException wrapped)
                e = Objects.requireNonNullElse(wrapped.getCause(), e); // unwrap the exception

            LOGGER.error(LOADING, "Failed to create mod instance. ModID: {}, class {}", getModId(), modClass.getName(), e);
            throw new ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmod", e, modClass);
        }

        try {
            LOGGER.trace(LOADING, "Injecting Automatic event subscribers for {}", getModId());
            AutomaticEventSubscriber.inject(this, this.scanResults, this.modClass.getClassLoader());
            LOGGER.trace(LOADING, "Completed Automatic event subscribers for {}", getModId());
        } catch (Throwable e) {
            LOGGER.error(LOADING, "Failed to register automatic subscribers. ModID: {}, class {}", getModId(), modClass.getName(), e);
            throw new ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmod", e, modClass);
        }
    }

    @Override
    public boolean matches(Object mod) {
        return mod == modInstance;
    }

    @Override
    public Object getMod() {
        return modInstance;
    }

    public IEventBus getEventBus() {
        return this.eventBus;
    }

    @Override
    protected <T extends Event & IModBusEvent> void acceptEvent(final T e) {
        try {
            LOGGER.trace(LOADING, "Firing event for modid {} : {}", this.getModId(), e);
            this.eventBus.post(e);
            LOGGER.trace(LOADING, "Fired event for modid {} : {}", this.getModId(), e);
        } catch (Throwable t) {
            LOGGER.error(LOADING,"Caught exception during event {} dispatch for modid {}", e, this.getModId(), t);
            throw new ModLoadingException(modInfo, modLoadingStage, "fml.modloading.errorduringevent", t);
        }
    }
}
