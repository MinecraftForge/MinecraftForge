/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.fml;

import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.config.IConfigSpecFactory;
import net.minecraftforge.common.config.ModConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ModLoadingContext
{
    private static ThreadLocal<ModLoadingContext> context = ThreadLocal.withInitial(ModLoadingContext::new);
    private Object languageExtension;

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

    /**
     * Register a new {@link ModConfig} with the given type.
     * This method will return the {@link ModConfigSpec} created for the ModConfig.
     *
     * @param type the {@link ModConfig.Type}
     * @return the {@link ModConfigSpec}
     */
    public ModConfigSpec registerConfig(ModConfig.Type type) {
        return registerConfig(type, Function.identity());
    }

    /**
     * Register a new config and call the given function after the {@link ModConfig} has been created.
     * This method will use the default {@link IConfigSpecFactory#createSpec(ModConfig)} method.
     *
     * @param type the {@link ModConfig.Type}
     * @param func the {@link Function} that turns a {@link ModConfigSpec} into another object
     * @return a new object as determined by the given Function
     */
    public <K extends ModConfigSpec, V> V registerConfig(ModConfig.Type type, Function<K, V> func) {
        return registerConfig(type, IConfigSpecFactory::createSpec, func);
    }

    /**
     * Register a new config and call the given function after the {@link ModConfig} has been created.
     *
     * @param type the {@link ModConfig.Type}
     * @param spec the {@link IConfigSpecFactory} to use
     * @param func the {@link Function} that turns a {@link ModConfigSpec} into another object
     * @return a new object as determined by the given Function
     */
    public <K extends ModConfigSpec, V> V registerConfig(ModConfig.Type type, IConfigSpecFactory<K> spec, Function<K, V> func) {
        ModConfig<K> modConfig = new ModConfig<K>(type, spec, getActiveContainer());
        getActiveContainer().addConfig(modConfig);
        return func.apply((K) modConfig.getSpec());
    }

    public <K extends ModConfigSpec, V> V registerConfig(ModConfig.Type type, IConfigSpecFactory<K> spec, String fileName, Function<K, V> func) {
        ModConfig<K> modConfig = new ModConfig<>(type, spec,getActiveContainer(), fileName);
        getActiveContainer().addConfig(modConfig);
        return func.apply((K) modConfig.getSpec());
    }


    @SuppressWarnings("unchecked")
    public <T> T extension() {
        return (T)languageExtension;
    }
}
