/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Supplier;

public class ModLoadingContext
{
    private static ThreadLocal<ModLoadingContext> context = ThreadLocal.withInitial(ModLoadingContext::new);
    private Object languageExtension;
    private ModLoadingStage stage;

    public static ModLoadingContext get() {
        return context.get();
    }

    private ModContainer activeContainer;

    public void setActiveContainer(final ModContainer container, final Object languageExtension) {
        this.activeContainer = container;
        this.languageExtension = languageExtension;
    }

    public ModContainer getActiveContainer() {
        return activeContainer == null ? ModList.get().getModContainerById("minecraft").orElseThrow(()->new RuntimeException("Where is minecraft???!")) : activeContainer;
    }

    public String getActiveNamespace() {
        return activeContainer == null ? "minecraft" : activeContainer.getNamespace();
    }

    /**
     * Register an {@link ExtensionPoint} with the mod container.
     * @param point The extension point to register
     * @param extension An extension operator
     * @param <T> The type signature of the extension operator
     */
    public <T> void registerExtensionPoint(ExtensionPoint<T> point, Supplier<T> extension) {
        getActiveContainer().registerExtensionPoint(point, extension);
    }

    public void registerConfig(ModConfig.Type type, ForgeConfigSpec spec) {
        getActiveContainer().addConfig(new ModConfig(type, spec, getActiveContainer()));
    }

    public void registerConfig(ModConfig.Type type, ForgeConfigSpec spec, String fileName) {
        getActiveContainer().addConfig(new ModConfig(type, spec, getActiveContainer(), fileName));
    }


    @SuppressWarnings("unchecked")
    public <T> T extension() {
        return (T)languageExtension;
    }
}
