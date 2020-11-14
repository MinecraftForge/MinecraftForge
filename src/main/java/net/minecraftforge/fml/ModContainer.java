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

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;

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
    protected final Map<ExtensionPoint, Supplier<?>> extensionPoints = new IdentityHashMap<>();
    protected final EnumMap<ModConfig.Type, ModConfig> configs = new EnumMap<>(ModConfig.Type.class);
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    protected Optional<Consumer<ModConfig.ModConfigEvent>> configHandler = Optional.empty();

    public ModContainer(IModInfo info)
    {
        this.modId = info.getModId();
        // TODO: Currently not reading namespace from configuration..
        this.namespace = this.modId;
        this.modInfo = info;
        this.modLoadingStage = ModLoadingStage.CONSTRUCT;
        // default displaytest extension checks for version string match
        registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(()->this.modInfo.getVersion().toString(),
                (incoming, isNetwork)->Objects.equals(incoming, this.modInfo.getVersion().toString())));
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
            final ModLoadingStage.EventGenerator<T> eventGenerator,
            final BiFunction<ModLoadingStage, Throwable, ModLoadingStage> stateChangeHandler,
            final Executor executor) {
        return CompletableFuture
                .runAsync(() -> {
                    ModLoadingContext.get().setActiveContainer(target, target.contextExtension.get());
                    target.activityMap.getOrDefault(target.modLoadingStage, ()->{}).run();
                    target.acceptEvent(eventGenerator.apply(target));
                }, executor)
                .whenComplete((mc, exception) -> {
                    target.modLoadingStage = stateChangeHandler.apply(target.modLoadingStage, exception);
                    ModLoadingContext.get().setActiveContainer(null, null);
                });
    }

    /**
     * @return the modinfo used to create this mod instance
     */
    public IModInfo getModInfo()
    {
        return modInfo;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getCustomExtension(ExtensionPoint<T> point) {
        return Optional.ofNullable((T)extensionPoints.getOrDefault(point,()-> null).get());
    }

    public <T> void registerExtensionPoint(ExtensionPoint<T> point, Supplier<T> extension)
    {
        extensionPoints.put(point, extension);
    }

    public void addConfig(final ModConfig modConfig) {
       configs.put(modConfig.getType(), modConfig);
    }

    public void dispatchConfigEvent(ModConfig.ModConfigEvent event) {
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
