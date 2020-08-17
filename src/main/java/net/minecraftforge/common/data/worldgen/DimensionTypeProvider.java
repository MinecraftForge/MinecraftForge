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
import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.common.data.CodecBackedProvider;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

public abstract class DimensionTypeProvider extends CodecBackedProvider<DimensionType>
{
    protected final DataGenerator generator;
    protected final String modid;
    protected final Map<ResourceLocation, Builder> builders = new HashMap<>();

    public DimensionTypeProvider(DataGenerator generator, ExistingFileHelper fileHelper, String modid)
    {
        super(DimensionType.field_235997_a_, fileHelper);
        this.generator = generator;
        this.modid = modid;
    }

    protected abstract void start();

    @Override
    public void act(DirectoryCache cache)
    {
        start();

        Path path = generator.getOutputFolder();

        builders.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, builder) ->
                this.save(builder.build(), cache, path.resolve("data/" + name.getNamespace() + "/dimension_type/" + name.getPath() + ".json"))
        ));

        this.fileHelper.reloadResources();
    }

    protected void put(ResourceLocation name, Builder builder)
    {
        builders.put(name, builder);
    }

    @Override
    public String getName()
    {
        return "DimensionTypes: " + modid;
    }

    /**
     * Info taken from <a href=https://minecraft.gamepedia.com/Custom_dimension#Dimension_type_syntax>the wiki</a>.
     * The default values are the Overworld's
     */
    public static class Builder
    {
        private OptionalLong fixedTime = OptionalLong.empty();
        private boolean hasSkylight = true;
        private boolean hasCeiling = false;
        private boolean ultraWarm = false;
        private boolean natural = true;
        private double coordinateScale = 1.0; //[1.0E-5F, 3.0E7D]
        private boolean piglinSafe = false;
        private boolean bedWorks = true;
        private boolean respawnAnchorWorks = false;
        private boolean hasRaids = true;
        private int logicalHeight = 256; //[0, 256]
        private ResourceLocation infiniburn = BlockTags.field_241277_aC_.func_230234_a_();
        private ResourceLocation effects = DimensionType.field_242710_a; //if unknown defaults to OW anyway. only 3 exists for vanilla.
        private float ambientLight = 0.0F;

        protected DimensionType build()
        {
            return new DimensionType(fixedTime, hasSkylight, hasCeiling, ultraWarm, natural, coordinateScale, piglinSafe, bedWorks, respawnAnchorWorks, hasRaids, logicalHeight, infiniburn, effects, ambientLight);
        }

        /**
         * Sets a fixed time for the dimension, Nether is at 18000 and The End is at 6000.
         */
        public Builder setFixedTime(Long time)
        {
            if(time > 24000 || time < 0)
                throw new IllegalArgumentException("Time range is between 0 and 24000");
            this.fixedTime = OptionalLong.of(time);
            return this;
        }

        public Builder hasSkylight(boolean value)
        {
            this.hasSkylight = value;
            return this;
        }

        /**
         * Whether the dimension has a bedrock ceiling. True also makes lava spread faster...
         */
        public Builder hasCeiling(boolean value)
        {
            this.hasCeiling = value;
            return this;
        }

        /**
         * If true, water evaporates and sponge dries. Lava also spread thinner.
         */
        public Builder isUltrawarm(boolean value)
        {
            this.ultraWarm = value;
            return this;
        }

        /**
         * False makes compasses spin randomly, true makes nether portals spawn zombified piglins.
         */
        public Builder isNatural(boolean value)
        {
            this.natural = value;
            return this;
        }

        /**
         * Scale multiplier when crossing dimensions. Nether has a value of 8.
         */
        public Builder setCoordinateScale(double scale)
        {
            if(scale > 3.0E7D || scale < 1.0E-5F)
                throw new IllegalArgumentException("Scale is too huge or too tiny!");
            this.coordinateScale = scale;
            return this;
        }

        /**
         * If false, Piglins become Zombified piglins in this dimension.
         */
        public Builder arePiglinSafe(boolean value)
        {
            this.piglinSafe = value;
            return this;
        }

        /**
         * If false, clicking on a bed in the dimension will show you an intentional game design.
         */
        public Builder doBedWorks(boolean value)
        {
            this.bedWorks = value;
            return this;
        }

        /**
         * If false, clicking on a respawn anchor in the dimension will show you an intentional game design.
         */
        public Builder doRespawnAnchorWork(boolean value)
        {
            this.respawnAnchorWorks = value;
            return this;
        }

        /**
         * Whether Bad Omen can cause a raid.
         */
        public Builder hasRaids(boolean value)
        {
            this.hasRaids = value;
            return this;
        }

        /**
         * Sets the maximum height chorus fruit and nether portals can bring players in this dimension.
         */
        public Builder setLogicalHeigh(int height)
        {
            if(height > 256 || height < 0)
                throw new IllegalArgumentException("Height must be between 0 and 256");
            this.logicalHeight = height;
            return this;
        }

        /**
         * Sets the resource location of a tag which will contain all of the blocks that can burn forever in this dimension.
         * If the tag does not exist, defaults to the overworld infiniburn.
         */
        public Builder setInfiniburnBlocks(ResourceLocation value)
        {
            this.infiniburn = value;
            return this;
        }

        /**
         * Sets the resource location of an {@link DimensionRenderInfo} that is retrieved through {@link DimensionRenderInfo#field_239208_a_}.
         * If it does not exists, defaults to the Overworld's
         */
        public Builder setEffects(ResourceLocation value)
        {
            this.effects = value;
            return this;
        }

        /**
         * Sets how much light the dimension has. Nether defaults to 0.1, the 2 others are 0.
         */
        public Builder setAmbientLight(float light)
        {
            this.ambientLight = light;
            return this;
        }
    }
}
