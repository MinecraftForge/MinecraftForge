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
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.CodecBackedProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * No builder for this class as StructureFeatures are simple to create, see {@link Structure}.
 */
public abstract class ConfiguredStructureFeatureProvider extends CodecBackedProvider<StructureFeature<?,?>>
{
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, StructureFeature<?, ?>> map = new HashMap<>();

    public ConfiguredStructureFeatureProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid)
    {
        //TODO This codec is dispatched for the vanilla STRUCTURE_FEATURE registry, and won't affect any mod added structures.
        super(StructureFeature.field_236267_a_, fileHelper);
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
                this.save(inst, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/configured_structure_feature/" + name.getPath() + ".json"))
        ));

        this.fileHelper.reloadResources();
    }

    public void put(ResourceLocation location, StructureFeature<?, ?> structure)
    {
        map.put(location, structure);
    }

    @Override
    public String getName()
    {
        return "Configured Structure Features: " + modid;
    }
}
