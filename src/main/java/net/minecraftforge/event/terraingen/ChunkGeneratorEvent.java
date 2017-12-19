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

import com.google.common.collect.Lists;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.IChunkGenerator;

import javax.annotation.Nullable;
import java.util.List;

public class ChunkGeneratorEvent extends Event
{
    private final IChunkGenerator gen;

    public ChunkGeneratorEvent(IChunkGenerator gen)
    {
        this.gen = gen;
    }

    public IChunkGenerator getGenerator() { return this.getGen(); }

    public IChunkGenerator getGen()
    {
        return gen;
    }

    /**
     * This event is fired when a chunks blocks are replaced by a biomes top and
     * filler blocks.
     *
     * You can set the result to DENY to prevent the default replacement.
     */
    @HasResult
    public static class ReplaceBiomeBlocks extends ChunkGeneratorEvent
    {
        private final int x;
        private final int z;
        private final ChunkPrimer primer;
        private final World world; // CAN BE NULL

        public ReplaceBiomeBlocks(IChunkGenerator chunkProvider, int x, int z, ChunkPrimer primer, World world)
        {
            super(chunkProvider);
            this.x = x;
            this.z = z;
            this.primer = primer;
            this.world = world;
        }

        public int getX() { return x; }
        public int getZ() { return z; }
        public ChunkPrimer getPrimer() { return primer; }
        public World getWorld() { return world; }
    }

    /**
     * This event is fired before a chunks terrain noise field is initialized.
     *
     * You can set the result to DENY to substitute your own noise field.
     */
    @HasResult
    public static class InitNoiseField extends ChunkGeneratorEvent
    {
        private double[] noisefield;
        private final int posX;
        private final int posY;
        private final int posZ;
        private final int sizeX;
        private final int sizeY;
        private final int sizeZ;

        public InitNoiseField(IChunkGenerator chunkProvider, double[] noisefield, int posX, int posY, int posZ, int sizeX, int sizeY, int sizeZ)
        {
            super(chunkProvider);
            this.setNoisefield(noisefield);
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.sizeZ = sizeZ;
        }

        public double[] getNoisefield() { return noisefield; }
        public void setNoisefield(double[] noisefield) { this.noisefield = noisefield; }
        public int getPosX() { return posX; }
        public int getPosY() { return posY; }
        public int getPosZ() { return posZ; }
        public int getSizeX() { return sizeX; }
        public int getSizeY() { return sizeY; }
        public int getSizeZ() { return sizeZ; }
    }

    /**
     * This event is fired during chunk generation as well as structure regeneration before any of the vanilla structure gens are executed.
     * If fired during recreateStructures the chunkprimer will be null
     * This event is fired regardless of mapFeaturesEnabled.
     */
    public static class Generate extends ChunkGeneratorEvent
    {
        private final World world;
        private final @Nullable ChunkPrimer primer;
        private final int x, z;

        public Generate(IChunkGenerator gen, World world, int x, int z, @Nullable ChunkPrimer primer)
        {
            super(gen);
            this.world = world;
            this.x = x;
            this.z = z;
            this.primer = primer;
        }

        public World getWorld()
        {
            return world;
        }

        public @Nullable ChunkPrimer getPrimer()
        {
            return primer;
        }

        public int getX()
        {
            return x;
        }

        public int getZ()
        {
            return z;
        }
    }

    /**
     * This event is fired when the chunk generator is asked for a list of possible creatures for a specific location.
     * (Vanilla checks if there is a structure which wants to spawn special creatures at the location)
     * <p>
     * To return your own spawn entries, add them to the list and cancel this event.
     */
    @Cancelable
    public static class GetPossibleCreatures extends ChunkGeneratorEvent
    {
        private final EnumCreatureType creatureType;
        private final List<Biome.SpawnListEntry> possibleCreatures;
        private final World world;
        private final BlockPos pos;

        public GetPossibleCreatures(IChunkGenerator gen, World world, EnumCreatureType creatureType, BlockPos pos)
        {
            super(gen);
            this.creatureType = creatureType;
            this.pos = pos;
            this.world = world;
            possibleCreatures = Lists.newArrayList();
        }

        public World getWorld()
        {
            return world;
        }

        public EnumCreatureType getCreatureType()
        {
            return creatureType;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public List<Biome.SpawnListEntry> getPossibleCreatures()
        {
            return possibleCreatures;
        }

    }

    /**
     * This event is fired if the chunk generator is asked if there is a structure at a specific location.
     * <p>
     * To return your own value set isInStructure and cancel this event.
     */
    @Cancelable
    public static class StructureCheck extends ChunkGeneratorEvent
    {
        private final World worldIn;
        private final String structureName;
        private final BlockPos pos;
        private boolean inStructure;

        public StructureCheck(IChunkGenerator gen, World worldIn, String structureName, BlockPos pos)
        {
            super(gen);
            this.worldIn = worldIn;
            this.structureName = structureName;
            this.pos = pos;
            inStructure = false;
        }

        public World getWorldIn()
        {
            return worldIn;
        }

        public String getStructureName()
        {
            return structureName;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public boolean isInStructure()
        {
            return inStructure;
        }

        public void setInStructure(boolean inStructure)
        {
            this.inStructure = inStructure;
        }
    }

    /**
     * This event is fired if the chunk generator is asked for a nearby structure.
     * <p>
     * To return your own location set the found pos and cancel this event.
     */
    @Cancelable
    public static class FindStructure extends ChunkGeneratorEvent
    {
        private final World worldIn;
        private final String structureName;
        private final BlockPos pos;
        private final boolean findUnexplored;
        @Nullable
        private BlockPos foundPos;

        public FindStructure(IChunkGenerator gen, World worldIn, String structureName, BlockPos pos, boolean findUnexplored)
        {
            super(gen);
            this.worldIn = worldIn;
            this.structureName = structureName;
            this.pos = pos;
            this.findUnexplored = findUnexplored;
            foundPos = null;
        }

        public World getWorldIn()
        {
            return worldIn;
        }

        public String getStructureName()
        {
            return structureName;
        }

        public BlockPos getPos()
        {
            return pos;
        }

        public boolean isFindUnexplored()
        {
            return findUnexplored;
        }

        @Nullable public BlockPos getFoundPos()
        {
            return foundPos;
        }

        public void setFoundPos(@Nullable BlockPos foundPos)
        {
            this.foundPos = foundPos;
        }
    }

}
