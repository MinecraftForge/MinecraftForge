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
import net.minecraft.world.gen.feature.template.IStructureProcessorType;
import net.minecraft.world.gen.feature.template.ProcessorLists;
import net.minecraft.world.gen.feature.template.StructureProcessorList;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * No builder as only a list is needed.
 * See {@link ProcessorLists} for creation examples.
 */
public abstract class ProcessorListProvider extends RegistryBackedProvider<StructureProcessorList>
{
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, StructureProcessorList> map = new HashMap<>();

    public ProcessorListProvider(DataGenerator generator, RegistryOpsHelper regOps, String modid)
    {
        super(IStructureProcessorType.field_242921_l, regOps, Registry.field_243554_aw);
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
                this.saveAndRegister(inst, name, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/processor_list/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation location, StructureProcessorList inst)
    {
        map.put(location, inst);
    }

    @Override
    public String getName()
    {
        return "Processor Lists: " + modid;
    }
}
