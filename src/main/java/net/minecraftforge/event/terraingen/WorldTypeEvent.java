/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

import javax.annotation.Nullable;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * WorldTypeEvent is fired when an event involving the world occurs.<br>
 * If a method utilizes this {@link Event} as its parameter, the method will
 * receive every child event of this class.<br>
 * <br>
 * {@link #world} the world for which the event is being fired. Can be null in some cases, for example when converting an old map to Anvil format. <br>
 * {@link #worldType} contains the WorldType of the world this event is occurring in.<br>
 * <br>
 * All children of this event are fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
 **/
public class WorldTypeEvent extends Event
{
    @Nullable
    private final World world;
    private final WorldType worldType;

    public WorldTypeEvent(@Nullable World world, WorldType worldType)
    {
        this.world = world;
        this.worldType = worldType;
    }

    @Nullable
    public World getWorld()
    {
        return this.world;
    }

    public WorldType getWorldType()
    {
        return worldType;
    }

    /**
     * BiomeSize is fired when vanilla Minecraft attempts to generate biomes.<br>
     * This event is fired during biome generation in
     * {@link GenLayer#initializeAllBiomeGenerators(long, WorldType, ChunkProviderSettings)}. <br>
     * <br>
     * {@link #world} the world for which the event is being fired. Can be null in some cases. <br>
     * {@link #worldType} the WorldType of the world for which the event is being fired. <br>
     * {@link #original} the original size of the Biome. <br>
     * If {@link #setNewSize()} is called with a new value, that value will be used for the Biome size. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
     **/
    public static class BiomeSize extends WorldTypeEvent
    {
        private final int originalSize;
        private int newSize;

        public BiomeSize(@Nullable World world, WorldType worldType, int original)
        {
            super(world, worldType);
            originalSize = original;
            setNewSize(original);
        }

        public int getOriginalSize()
        {
            return originalSize;
        }

        public int getNewSize()
        {
            return newSize;
        }

        public void setNewSize(int newSize)
        {
            this.newSize = newSize;
        }
    }

    /**
     * InitBiomeGens is fired when vanilla Minecraft attempts to initialize the biome providers.<br>
     * This event is fired just during biome provider initialization in
     * {@link BiomeProvider#BiomeProvider(World, long, WorldType, String)}. <br>
     * <br>
     * {@link #world} the world for which the event is being fired. Can be null in some cases. <br>
     * {@link #worldType} the WorldType of the world for which the event is being fired. <br>
     * {@link #seed} the seed of the world. <br>
     * {@link #original} the array of GenLayers originally intended for this Biome generation. <br>
     * If {@link #setNewBiomeGens()} is called with an array of GenLayers, those will then be used for the Biome generator. <br>
     * <br>
     * This event is not {@link Cancelable}.<br>
     * <br>
     * This event does not have a result. {@link HasResult} <br>
     * <br>
     * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.<br>
     **/
    public static class InitBiomeGens extends WorldTypeEvent
    {
        private final long seed;
        private final GenLayer[] originalBiomeGens;
        private GenLayer[] newBiomeGens;

        public InitBiomeGens(@Nullable World world, WorldType worldType, long seed, GenLayer[] original)
        {
            super(world, worldType);
            this.seed = seed;
            originalBiomeGens = original;
            setNewBiomeGens(original.clone());
        }

        public long getSeed()
        {
            return seed;
        }

        public GenLayer[] getOriginalBiomeGens()
        {
            return originalBiomeGens;
        }

        public GenLayer[] getNewBiomeGens()
        {
            return newBiomeGens;
        }

        public void setNewBiomeGens(GenLayer[] newBiomeGens)
        {
            this.newBiomeGens = newBiomeGens;
        }
    }
}
