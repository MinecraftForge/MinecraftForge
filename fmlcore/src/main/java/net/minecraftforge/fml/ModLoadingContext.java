/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class ModLoadingContext
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ThreadLocal<ModLoadingContext> context = ThreadLocal.withInitial(ModLoadingContext::new);
    private ModContainer activeContainer;
    private Object languageExtension;
    private ModLoadingStage stage;

    /**
     * @deprecated Use {@link ModLoadingContext} or any class that extends it, in your mod constructor.
     */
    @Deprecated(forRemoval = true, since="1.21.1")
    public static ModLoadingContext get() {
        return context.get();
    }

    /**
     * @deprecated Going to be moved to ForgeHooks for Internal use.
     */
    @Deprecated(forRemoval = true, since="1.21.1")
    public void setActiveContainer(final ModContainer container) {
        this.activeContainer = container;
        this.languageExtension = container == null ? null : container.contextExtension.get();
    }

    /**
     * Going to be moved to ForgeHooks for Internal use.
     * @deprecated Override/Use {@link ModLoadingContext#getContainer()}
     */
    @Deprecated(forRemoval = true, since="1.21.1")
    public ModContainer getActiveContainer() {
        return activeContainer == null ? ModList.get().getModContainerById("minecraft").orElseThrow(()->new RuntimeException("Where is minecraft???!")) : activeContainer;
    }

    /**
     * @deprecated Going to be moved to ForgeHooks for Internal use.
     */
    @Deprecated(forRemoval = true, since="1.21.1")
    public String getActiveNamespace() {
        return activeContainer == null ? "minecraft" : activeContainer.getNamespace();
    }

    /**
     * @return {@link ModLoadingContext#getActiveContainer()} by default.
     */
    public ModContainer getContainer() {
        return getActiveContainer();
    }

    /**
     * Register an {@link IExtensionPoint} with the mod container.
     * @param point The extension point to register
     * @param extension An extension operator
     * @param <T> The type signature of the extension operator
     */
    public <T extends Record & IExtensionPoint<T>> void registerExtensionPoint(Class<? extends IExtensionPoint<T>> point, Supplier<T> extension) {
        getActiveContainer().registerExtensionPoint(point, extension);
    }

    /**
     * Register a {@link IExtensionPoint.DisplayTest} with the mod container.
     * <p>A shorthand for registering a DisplayTest with {@link #registerExtensionPoint(Class, Supplier)}.</p>
     * @param displayTest The {@link IExtensionPoint.DisplayTest} to register
     */
    public void registerDisplayTest(IExtensionPoint.DisplayTest displayTest) {
        getActiveContainer().registerDisplayTest(() -> displayTest);
    }

    /**
     * Register a {@link IExtensionPoint.DisplayTest} with the mod container.
     * <p>A shorthand for registering a DisplayTest supplier with {@link #registerExtensionPoint(Class, Supplier)}.</p>
     * @param displayTest The {@link Supplier<IExtensionPoint.DisplayTest>} to register
     */
    public void registerDisplayTest(Supplier<IExtensionPoint.DisplayTest> displayTest) {
        getActiveContainer().registerDisplayTest(displayTest);
    }

    /**
     * Register a {@link IExtensionPoint.DisplayTest} with the mod container.
     * <p>A shorthand for registering a DisplayTest with {@link #registerExtensionPoint(Class, Supplier)} that also
     * creates the DisplayTest instance for you using the provided parameters.</p>
     * @see IExtensionPoint.DisplayTest#DisplayTest(String, BiPredicate)
     */
    public void registerDisplayTest(String version, BiPredicate<String, Boolean> remoteVersionTest) {
        getActiveContainer().registerDisplayTest(new IExtensionPoint.DisplayTest(version, remoteVersionTest));
    }

    /**
     * Register a {@link IExtensionPoint.DisplayTest} with the mod container.
     * <p>A shorthand for registering a DisplayTest with {@link #registerExtensionPoint(Class, Supplier)} that also
     * creates the DisplayTest instance for you using the provided parameters.</p>
     * @see IExtensionPoint.DisplayTest#DisplayTest(Supplier, BiPredicate)
     */
    public void registerDisplayTest(Supplier<String> suppliedVersion, BiPredicate<String, Boolean> remoteVersionTest) {
        getActiveContainer().registerDisplayTest(new IExtensionPoint.DisplayTest(suppliedVersion, remoteVersionTest));
    }

    public void registerConfig(ModConfig.Type type, IConfigSpec<?> spec) {
        if (spec.isEmpty())
        {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            LOGGER.debug("Attempted to register an empty config for type {} on mod {}", type, getActiveContainer().getModId());
            return;
        }

        getActiveContainer().addConfig(new ModConfig(type, spec, getActiveContainer()));
    }

    public void registerConfig(ModConfig.Type type, IConfigSpec<?> spec, String fileName) {
        if (spec.isEmpty())
        {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            LOGGER.debug("Attempted to register an empty config for type {} on mod {} using file name {}", type, getActiveContainer().getModId(), fileName);
            return;
        }

        getActiveContainer().addConfig(new ModConfig(type, spec, getActiveContainer(), fileName));
    }


    @SuppressWarnings("unchecked")
    public <T> T extension() {
        return (T)languageExtension;
    }
}
