/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.event;

import net.minecraft.util.registry.DynamicRegistries;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;

/**
 * <p>Fired after datapacks are loaded, and after the {@link DynamicRegistries} are populated with all their data, but before the server is loaded.</p>
 *
 * <p>Using this event allows mods to interact with datapack worldgen objects, among other things.<br>
 * For example, manually inserting a JSON feature into a vanilla biome.</p>
 *
 * <p>This event is not {@linkplain net.minecraftforge.eventbus.api.Cancelable cancellable},
 * and does not {@linkplain HasResult have a result}.</p>
 */
public class DynamicRegistriesLoadedEvent extends Event {
    @Nonnull
    private final DynamicRegistries.Impl registries;

    public DynamicRegistriesLoadedEvent(final @Nonnull DynamicRegistries.Impl registries)
    {
        this.registries = registries;
    }

    public @Nonnull DynamicRegistries.Impl getRegistries() { return registries; }

}
