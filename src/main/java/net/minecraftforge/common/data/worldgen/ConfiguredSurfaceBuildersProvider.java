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
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * The Builder class is for the vanilla implementation {@link ISurfaceBuilderConfig}
 */
public abstract class ConfiguredSurfaceBuildersProvider extends RegistryBackedProvider<ConfiguredSurfaceBuilder<?>>
{
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, ConfiguredSurfaceBuilder<?>> map = new HashMap<>();

    public ConfiguredSurfaceBuildersProvider(DataGenerator generator, RegistryOpsHelper regOps, String modid)
    {
        //TODO This codec is dispatched for the vanilla SURFACE_BUILDER registry, and won't affect any mod added SURFACE_BUILDERs.
        super(ConfiguredSurfaceBuilder.field_237168_a_, regOps, Registry.field_243550_as);
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
                this.saveAndRegister(inst, name, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/configured_surface_builder/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation location, ConfiguredSurfaceBuilder<?> inst)
    {
        map.put(location, inst);
    }

    @Override
    public String getName()
    {
        return "Configured Surface Builders: " + modid;
    }

    /**
     * Only one implementation of {@link ISurfaceBuilderConfig} exists by default, this builder is designed for that.
     */
    public static class Builder
    {
        private BlockState topMaterial = Blocks.GRASS_BLOCK.getDefaultState();
        private BlockState underMaterial = Blocks.DIRT.getDefaultState();
        private BlockState underWaterMaterial = Blocks.GRAVEL.getDefaultState();
        private SurfaceBuilder<SurfaceBuilderConfig> surfaceBuilder = SurfaceBuilder.DEFAULT;

        protected ConfiguredSurfaceBuilder<SurfaceBuilderConfig> build()
        {
            return surfaceBuilder.func_242929_a(new SurfaceBuilderConfig(topMaterial, underMaterial, underWaterMaterial));
        }

        public Builder setSurfaceBuilder(SurfaceBuilder<SurfaceBuilderConfig> surfaceBuilder)
        {
            this.surfaceBuilder = surfaceBuilder;
            return this;
        }

        public Builder setTopMaterial(BlockState state)
        {
            this.topMaterial = state;
            return this;
        }

        public Builder setUnderMaterial(BlockState state)
        {
            this.underMaterial = state;
            return this;
        }

        public Builder setUnderWaterMaterial(BlockState state)
        {
            this.underWaterMaterial = state;
            return this;
        }
    }
}
