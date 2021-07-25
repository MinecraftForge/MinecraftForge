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

package net.minecraftforge.event.world;

import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fires on server start before levels are added to the save. Register custom
 * dimensions here.
 */
public class RegisterDimensionsEvent extends Event
{
    private final MappedRegistry<LevelStem> levelStemRegistry;
    private final long overworldSeed;
    private final RegistryAccess.RegistryHolder registryHolder;

    public RegisterDimensionsEvent(MappedRegistry<LevelStem> levelStemRegistry, long overworldSeed, RegistryAccess.RegistryHolder registryHolder)
    {
        this.levelStemRegistry = levelStemRegistry;
        this.overworldSeed = overworldSeed;
        this.registryHolder = registryHolder;
    }

    /**
     * @return The level stem (dimension) registry for the current server. Use this
     *         to register your level stems.
     */
    public MappedRegistry<LevelStem> getLevelStemRegistry()
    {
        return this.levelStemRegistry;
    }

    /**
     * @return The seed of the overworld.
     */
    public long getOverworldSeed()
    {
        return this.overworldSeed;
    }

    /**
     * @return The current server's registry data, primarily useful for biome
     *         access.
     */
    public RegistryAccess.RegistryHolder getRegistryHolder()
    {
        return this.registryHolder;
    }
}
