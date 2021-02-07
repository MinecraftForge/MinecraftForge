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
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Features;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * No builder for this class as ConfiguredFeatures are simple to create, see {@link Features}.
 * See <a href=https://minecraft.gamepedia.com/Custom_world_generation#Features>the wiki</a> for more info.
 */
public abstract class ConfiguredFeatureProvider extends RegistryBackedProvider<ConfiguredFeature<?,?>>
{
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, ConfiguredFeature<?, ?>> map = new HashMap<>();

    public ConfiguredFeatureProvider(DataGenerator generator, RegistryOpsHelper regOps, String modid)
    {
        //TODO This codec is dispatched for the vanilla FEATURE registry, and won't affect any mod added features.
        super(ConfiguredFeature.field_242763_a, regOps, Registry.CONFIGURED_FEATURE_KEY);
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
                this.saveAndRegister(inst, name, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/configured_feature/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation location, ConfiguredFeature<?, ?> feature)
    {
        map.put(location, feature);
    }

    @Override
    public String getName()
    {
        return "Configured Features: " + modid;
    }
}
