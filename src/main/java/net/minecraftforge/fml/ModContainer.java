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

package net.minecraftforge.fml;

import net.minecraftforge.fml.language.IModInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    protected final IModInfo modInfo;
    protected ModLoadingStage modLoadingStage;
    protected final Map<ModLoadingStage, Consumer<LifecycleEventProvider.LifecycleEvent>> triggerMap;
    protected final Map<ExtensionPoint, Supplier<?>> extensionPoints = new IdentityHashMap<>();
    protected final List<Throwable> modLoadingError;
    public ModContainer(IModInfo info)
    {
        this.modId = info.getModId();
        this.modInfo = info;
        this.triggerMap = new HashMap<>();
        this.modLoadingError = new ArrayList<>();
        this.modLoadingStage = ModLoadingStage.CONSTRUCT;
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
    public final String getPrefix()
    {
        return modId;
    }

    /**
     * @return The current loading stage for this mod
     */
    public ModLoadingStage getCurrentState()
    {
        return modLoadingStage;
    }

    /**
     * Transition the mod to this event if possible.
     * @param event to transition to
     */
    public final void transitionState(LifecycleEventProvider.LifecycleEvent event)
    {
        if (modLoadingStage == event.fromStage())
        {
            try
            {
                triggerMap.getOrDefault(modLoadingStage, e->{}).accept(event);
                modLoadingStage = event.toStage();
            }
            catch (RuntimeException e)
            {
                modLoadingError.add(e);
                modLoadingStage = ModLoadingStage.ERROR;
            }
        }
    }

    /**
     * @return the modinfo used to create this mod instance
     */
    public IModInfo getModInfo()
    {
        return modInfo;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> getCustomExtension(ExtensionPoint point) {
        return Optional.ofNullable((T)extensionPoints.getOrDefault(point,()-> null).get());
    }

    public <T> void registerExtensionPoint(ExtensionPoint point, Supplier<T> extension)
    {
        extensionPoints.put(point, extension);
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
}
