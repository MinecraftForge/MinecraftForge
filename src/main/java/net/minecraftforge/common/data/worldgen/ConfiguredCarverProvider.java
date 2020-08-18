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
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.structure.Structure;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * No builder for this class as StructureFeatures are simple to create, see {@link Structure}.
 */
public abstract class ConfiguredCarverProvider extends RegistryBackedProvider<ConfiguredCarver<?>>
{
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, ConfiguredCarver<?>> builders = new HashMap<>();

    public ConfiguredCarverProvider(DataGenerator generator, RegistryOpsHelper helper, String modid)
    {
        //TODO This codec is dispatched for the vanilla CARVER registry, and won't affect any mod added carvers.
        super(ConfiguredCarver.field_236235_a_, helper, Registry.field_243551_at);
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache)
    {
        start();

        Path path = generator.getOutputFolder();

        builders.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, inst) ->
                this.saveAndRegister(inst, name, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/configured_carver/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation location, ConfiguredCarver<?> inst)
    {
        builders.put(location, inst);
    }


    @Override
    public String getName()
    {
        return "Configured Carvers: " + modid;
    }
}
