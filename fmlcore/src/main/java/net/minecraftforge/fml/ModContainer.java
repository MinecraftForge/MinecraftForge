/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.loading.progress.ProgressMeter;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The container that wraps around mods in the system.
 * <p>
 * The philosophy is that individual mod implementation technologies should not
 * impact the actual loading and management of mod code. This class provides
 * a mechanism by which we can wrap actual mod code so that the loader and other
 * facilities can treat mods at arms length.
 * </p>
 *
 * @author cpw
 *
 */

public abstract class ModContainer
{
    protected final String modId;
    protected final String namespace;
    protected final IModInfo modInfo;
    protected ModLoadingStage modLoadingStage;
    protected Supplier<?> contextExtension;
    protected final Map<ModLoadingStage, Runnable> activityMap = new HashMap<>();
    protected final Map<Class<? extends IExtensionPoint<?>>, Supplier<?>> extensionPoints = new IdentityHashMap<>();
    protected final EnumMap<ModConfig.Type, ModConfig> configs = new EnumMap<>(ModConfig.Type.class);
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected Optional<Consumer<IConfigEvent>> configHandler = Optional.empty();

    public ModContainer(IModInfo info)
    {
        this.modId = info.getModId();
        // TODO: Currently not reading namespace from configuration..
        this.namespace = this.modId;
        this.modInfo = info;
        this.modLoadingStage = ModLoadingStage.CONSTRUCT;

        final String displayTestString = info.getConfig().<String>getConfigElement("displayTest").orElse("MATCH_VERSION"); // missing defaults to DEFAULT type
        Supplier<IExtensionPoint.DisplayTest> displayTestSupplier = switch (displayTestString) {
            case "MATCH_VERSION" -> // default displaytest checks for version string match
                    () -> new IExtensionPoint.DisplayTest(() -> this.modInfo.getVersion().toString(),
                        (incoming, isNetwork) -> Objects.equals(incoming, this.modInfo.getVersion().toString()));
            case "IGNORE_SERVER_VERSION" -> // Ignores any version information coming from the server - use for server only mods
                    () -> new IExtensionPoint.DisplayTest(() -> IExtensionPoint.DisplayTest.IGNORESERVERONLY, (incoming, isNetwork) -> true);
            case "IGNORE_ALL_VERSION" -> // Ignores all information and provides no information
                    () -> new IExtensionPoint.DisplayTest(() -> "", (incoming, isNetwork) -> true);
            case "NONE" -> null; // NO display test at all - use this if you're going to do your own display test
            default -> // any other value throws an exception
                    throw new IllegalArgumentException("Invalid displayTest value supplied in mods.toml");
        };
        if (displayTestSupplier != null)
            registerExtensionPoint(IExtensionPoint.DisplayTest.class, displayTestSupplier);
        else
            extensionPoints.remove(IExtensionPoint.DisplayTest.class);
    }

    /**
     * Errored container state, used for filtering. Does nothing.
     */
    ModContainer()
    {
        this.modLoadingStage = ModLoadingStage.ERROR;
        modId = "BROKEN";
        namespace = "BROKEN";
        modInfo = null;
    }
    /**
     * @return the modid for this mod
     */
    public final String getModId()
    {
        return modId;
    }

    /**
     * @return the resource prefix for the mod
     */
    public final String getNamespace()
    {
        return namespace;
    }

    /**
     * @return The current loading stage for this mod
     */
    public ModLoadingStage getCurrentState()
    {
        return modLoadingStage;
    }

    public static <T extends Event & IModBusEvent> CompletableFuture<Void> buildTransitionHandler(
            final ModContainer target,
            final IModStateTransition.EventGenerator<T> eventGenerator,
            final ProgressMeter progressBar,
            final BiFunction<ModLoadingStage, Throwable, ModLoadingStage> stateChangeHandler,
            final Executor executor) {
        return CompletableFuture
                .runAsync(() -> {
                    ModLoadingContext.get().setActiveContainer(target);
                    target.activityMap.getOrDefault(target.modLoadingStage, ()->{}).run();
                    target.acceptEvent(eventGenerator.apply(target));
                }, executor)
                .whenComplete((mc, exception) -> {
                    target.modLoadingStage = stateChangeHandler.apply(target.modLoadingStage, exception);
                    progressBar.increment();
                    ModLoadingContext.get().setActiveContainer(null);
                });
    }

    public IModInfo getModInfo()
    {
        return modInfo;
    }

    @SuppressWarnings("unchecked")
    public <T extends Record> Optional<T> getCustomExtension(Class<? extends IExtensionPoint<T>> point) {
        return Optional.ofNullable((T)extensionPoints.getOrDefault(point,()-> null).get());
    }

    public <T extends Record & IExtensionPoint<T>> void registerExtensionPoint(Class<? extends IExtensionPoint<T>> point, Supplier<T> extension)
    {
        extensionPoints.put(point, extension);
    }

    public void addConfig(final ModConfig modConfig) {
       configs.put(modConfig.getType(), modConfig);
    }

    public void dispatchConfigEvent(IConfigEvent event) {
        configHandler.ifPresent(configHandler->configHandler.accept(event));
    }

    /**
     * Does this mod match the supplied mod?
     *
     * @param mod to compare
     * @return if the mod matches
     */
    public abstract boolean matches(Object mod);

    /**
     * @return the mod object instance
     */
    public abstract Object getMod();

    /**
     * Accept an arbitrary event for processing by the mod. Probably posted to an event bus in the lower level container.
     * @param e Event to accept
     */
    protected <T extends Event & IModBusEvent> void acceptEvent(T e) {}
}
