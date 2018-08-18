/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.event.terraingen;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.BiomeDecorator;
import net.minecraft.world.biome.Biome;

/**
 * BiomeEvent is fired whenever an event involving biomes occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}
 * unless stated otherwise in their Javadocs.
 **/
public class BiomeEvent extends Event
{
    private final Biome biome;

    public BiomeEvent(Biome biome)
    {
        this.biome = biome;
    }

    public Biome getBiome()
    {
        return biome;
    }

    /**
     * CreateDecorator is fired when a BiomeDecorator is created.<br>
     * This event is fired whenever a BiomeDecorator is created in
     * {@link DeferredBiomeDecorator#fireCreateEventAndReplace(Biome)}.<br>
     * <br>
     * {@link #originalBiomeDecorator} contains the original BiomeDecorator that would be used in vanilla.
     * {@link #newBiomeDecorator} contains the new BiomeDecoration to be used by Minecraft.
     * <br>
     * This event is not {@link Cancelable}.
     * <br>
     * This event does not have a result. {@link HasResult}
     * <br>
     * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.
     **/
    public static class CreateDecorator extends BiomeEvent
    {
        private final BiomeDecorator originalBiomeDecorator;
        private BiomeDecorator newBiomeDecorator;

        public CreateDecorator(Biome biome, BiomeDecorator original)
        {
            super(biome);
            originalBiomeDecorator = original;
            setNewBiomeDecorator(original);
        }

        public BiomeDecorator getOriginalBiomeDecorator()
        {
            return originalBiomeDecorator;
        }

        public BiomeDecorator getNewBiomeDecorator()
        {
            return newBiomeDecorator;
        }

        public void setNewBiomeDecorator(BiomeDecorator newBiomeDecorator)
        {
            this.newBiomeDecorator = newBiomeDecorator;
        }
    }

    /**
     * BiomeColor is fired whenever an event involving biome colors occurs. <br>
     * If a method utilizes this {@link Event} as its parameter, the method will
     * receive every child event of this class.<br>
     * <br>
     * All children of this event are fired on the {@link MinecraftForge#EVENT_BUS}.
     **/
    public static class BiomeColor extends BiomeEvent
    {
        private final int originalColor;
        private int newColor;

        public BiomeColor(Biome biome, int original)
        {
            super(biome);
            originalColor = original;
            setNewColor(original);
        }

        public int getOriginalColor()
        {
            return originalColor;
        }

        public int getNewColor()
        {
            return newColor;
        }

        public void setNewColor(int newColor)
        {
            this.newColor = newColor;
        }
    }

    /**
     * This event is fired when the village generator attempts to choose a block ID
     * based on the village's biome.
     *
     * You can cancel the event to override default values
     */
    @HasResult
    public static class GetVillageBlockID extends BiomeEvent
    {
        private final IBlockState original;
        private IBlockState replacement;

        public GetVillageBlockID(Biome biome, IBlockState original)
        {
            super(biome);
            this.original = original;
        }

        public IBlockState getOriginal()
        {
            return original;
        }

        public IBlockState getReplacement()
        {
            return replacement;
        }

        public void setReplacement(IBlockState replacement)
        {
            this.replacement = replacement;
        }
    }

    /**
     * This event is fired when a biome is queried for its grass color.
     */
    public static class GetGrassColor extends BiomeColor
    {
        public GetGrassColor(Biome biome, int original)
        {
            super(biome, original);
        }
    }

    /**
     * This event is fired when a biome is queried for its grass color.
     */
    public static class GetFoliageColor extends BiomeColor
    {
        public GetFoliageColor(Biome biome, int original)
        {
            super(biome, original);
        }
    }

    /**
     * This event is fired when a biome is queried for its water color.
     */
    public static class GetWaterColor extends BiomeColor
    {
        public GetWaterColor(Biome biome, int original)
        {
            super(biome, original);
        }
    }
}
