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

package net.minecraftforge.common.data.worldgen;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Vanilla already provides a builder for biomes {@link Biome.Builder}
 * There are also builders for {@link BiomeAmbience.Builder},
 * for {@link MobSpawnInfo.Builder} and {@link BiomeGenerationSettings.Builder},
 * which are needed to create a Biome.
 *
 * The {@link BiomeMaker} class also provides helper functions for creating specific biomes.
 *
 * To use newly created objects from other DataProviders, add this provider after the others.
 * See {@link RegistryOpsHelper#getObject} for usage.
 *
 * See <a href=https://minecraft.gamepedia.com/Custom_world_generation#Biome>the wiki</a> for more details
 * on biome parameters.
 */
public abstract class BiomeDataProvider extends RegistryBackedProvider<Biome>
{
    protected final DataGenerator generator;
    protected final String modid;
    protected final Map<ResourceLocation, Biome> map = new HashMap<>();

    protected BiomeDataProvider(DataGenerator generator, RegistryOpsHelper helper, String modid)
    {
        super(Biome.CODEC, helper, Registry.BIOME_KEY);
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache)
    {
        start();

        Path path = generator.getOutputFolder();

        map.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, inst) ->
                this.saveAndRegister(inst, name, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/biome/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation location, Biome biome)
    {
        map.put(location, biome);
    }

    @Override
    public String getName()
    {
        return "Biomes : " + modid;
    }
}
