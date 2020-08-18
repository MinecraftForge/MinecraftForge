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

import com.mojang.datafixers.util.Pair;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern;
import net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.structure.PlainsVillagePools;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Referred to as JigsawPools or TemplatePools by the wiki.
 */
public abstract class JigsawPatternProvider extends RegistryBackedProvider<JigsawPattern>
{
    private final DataGenerator generator;
    private final String modid;
    protected final Map<ResourceLocation, Builder> map = new HashMap<>();

    public JigsawPatternProvider(DataGenerator generator, RegistryOpsHelper regOps, String modid)
    {
        super(JigsawPattern.field_236852_a_, regOps, Registry.field_243555_ax);
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache)
    {
        start();

        Path path = generator.getOutputFolder();

        map.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, builder) ->
                this.saveAndRegister(builder.build(), name, cache, path.resolve("data/" + name.getNamespace() + "/worldgen/template_pool/" + name.getPath() + ".json"))
        ));
    }

    public void put(ResourceLocation location, Builder builder)
    {
        map.put(location, builder);
    }

    @Override
    public String getName()
    {
        return "Jigsaw Patterns: " + modid;
    }

    public static class Builder
    {
        private ResourceLocation name = JigsawPatternRegistry.field_244091_a.func_240901_a_(); //empty
        private ResourceLocation fallBack = JigsawPatternRegistry.field_244091_a.func_240901_a_(); //empty
        private final List<Pair<JigsawPiece, Integer>> weightedList = new ArrayList<>();

        protected JigsawPattern build()
        {
            return new JigsawPattern(name, fallBack, weightedList);
        }

        public Builder setName(ResourceLocation name)
        {
            this.name = name;
            return this;
        }

        public Builder setFallBack(ResourceLocation fallBack)
        {
            this.fallBack = fallBack;
            return this;
        }

        /**
         * JigsawPiece has a bunch of helpers that returns this mess.
         * See {@link PlainsVillagePools} for examples.
         */
        public Builder addAll(List<Pair<Function<JigsawPattern.PlacementBehaviour, ? extends JigsawPiece>, Integer>> weightedList, JigsawPattern.PlacementBehaviour behaviour)
        {
            weightedList.forEach(p -> this.weightedList.add(p.mapFirst(f -> f.apply(behaviour))));
            return this;
        }

        public Builder add(JigsawPiece piece, Integer weight)
        {
            weightedList.add(Pair.of(piece, weight));
            return this;
        }

        public Builder addAll(List<Pair<JigsawPiece, Integer>> weightedList)
        {
            this.weightedList.addAll(weightedList);
            return this;
        }
    }
}
