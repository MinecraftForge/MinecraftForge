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

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate;
import net.minecraftforge.event.terraingen.OreGenEvent.GenerateMinable;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

public abstract class TerrainGen
{
    public static <T extends InitNoiseGensEvent.Context> T getModdedNoiseGenerators(World world, Random rand, T original)
    {
        InitNoiseGensEvent<T> event = new InitNoiseGensEvent<T>(world, rand, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getNewValues();
    }

    public static MapGenBase getModdedMapGen(MapGenBase original, InitMapGenEvent.EventType type)
    {
        InitMapGenEvent event = new InitMapGenEvent(type, original);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getNewGen();
    }

    public static boolean populate(IChunkGenerator chunkProvider, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated, Populate.EventType type)
    {
        PopulateChunkEvent.Populate event = new PopulateChunkEvent.Populate(chunkProvider, world, rand, chunkX, chunkZ, hasVillageGenerated, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    /**
     * This call lacks amount data and should not be used any longer.
     * It's kept for legacy purposes.
     */
    @Deprecated
    public static boolean decorate(World world, Random rand, BlockPos pos, Decorate.EventType type)
    {
        Decorate event = new Decorate(world, rand, pos, type);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    public static int decorate(Decorate event)
    {
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY ? event.getModifiedAmount() : -1;
    }

    public static int mushroom(World world, Random rand, BlockPos pos, WorldGenerator generator, Decorate.Generator.Position position, int amount)
    {
        return decorate(new Decorate.Generator(world, rand, pos, Decorate.EventType.SHROOM, amount, generator, position));
    }

    public static int decorate(World world, Random rand, BlockPos pos, Decorate.EventType type, int amount, WorldGenerator generator, Decorate.Generator.Position position)
    {
        return decorate(new Decorate.Generator(world, rand, pos, type, amount, generator, position));
    }

    public static int decorate(World world, Random rand, BlockPos pos, Decorate.EventType type, int amount)
    {
        return decorate(new Decorate(world, rand, pos, type, amount));
    }

    public static boolean generateOre(World world, Random rand, WorldGenerator generator, BlockPos pos, GenerateMinable.EventType type)
    {
        GenerateMinable event = new GenerateMinable(world, rand, generator, pos, type);
        MinecraftForge.ORE_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }

    public static boolean saplingGrowTree(World world, Random rand, BlockPos pos)
    {
        SaplingGrowTreeEvent event = new SaplingGrowTreeEvent(world, rand, pos);
        MinecraftForge.TERRAIN_GEN_BUS.post(event);
        return event.getResult() != Result.DENY;
    }
}
