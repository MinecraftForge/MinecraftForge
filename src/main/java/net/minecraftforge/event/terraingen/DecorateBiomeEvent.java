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

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**DecorateBiomeEvent is fired when a BiomeDecorator is created.
 * <br>
 * This event is fired whenever a BiomeDecorator is created in
 * {@link DeferredBiomeDecorator#fireCreateEventAndReplace(Biome)}.<br>
 * <br>
 * {@link #world} contains the world that is being decorated. <br>
 * {@link #rand} contains an instance of Random to be used. <br>
 * {@link #pos} contains the coordinates of the Chunk being decorated. <br>
 * <br>
 * This event is not {@link Cancelable}.
 * <br>
 * This event does not have a result. {@link HasResult}
 * <br>
 * This event is fired on the {@link MinecraftForge#TERRAIN_GEN_BUS}.
 **/
public class DecorateBiomeEvent extends Event
{
    private final World world;
    private final Random rand;
    private final BlockPos pos;

    public DecorateBiomeEvent(World world, Random rand, BlockPos pos)
    {
        this.world = world;
        this.rand = rand;
        this.pos = pos;
    }

    public World getWorld()
    {
        return world;
    }

    public Random getRand()
    {
        return rand;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    /**
     * This event is fired before a chunk is decorated with a biome feature.
     */
    public static class Pre extends DecorateBiomeEvent
    {
        public Pre(World world, Random rand, BlockPos pos)
        {
            super(world, rand, pos);
        }
    }

    /**
     * This event is fired after a chunk is decorated with a biome feature.
     */
    public static class Post extends DecorateBiomeEvent
    {
        public Post(World world, Random rand, BlockPos pos)
        {
            super(world, rand, pos);
        }
    }

    /**
     * This event is fired when a chunk is decorated with a biome feature.
     *
     * You can set the result to DENY to prevent the default biome decoration.
     */
    @HasResult
    public static class Decorate extends DecorateBiomeEvent
    {
        public EventType getType()
        {
            return type;
        }

        public boolean hasAmountData()
        {
            return totalAmount != null && modifiedAmount != null;
        }

        public int getTotalAmount()
        {
            if (totalAmount == null)
                throw new IllegalStateException();

            return totalAmount;
        }

        public int getModifiedAmount()
        {
            if (modifiedAmount == null)
                throw new IllegalStateException();

            return modifiedAmount;
        }

        public void setModifiedAmount(int modifiedAmount)
        {
            this.modifiedAmount = modifiedAmount;
        }

        /** Use CUSTOM to filter custom event types
         */
        public static enum EventType { BIG_SHROOM, CACTUS, CLAY, DEAD_BUSH, DESERT_WELL, LILYPAD, FLOWERS, FOSSIL, GRASS, ICE, LAKE_WATER, LAKE_LAVA, PUMPKIN, REED, ROCK, SAND, SAND_PASS2, SHROOM, TREE, CUSTOM }

        private final EventType type;

        @Nullable
        private final Integer totalAmount;
        @Nullable
        private Integer modifiedAmount;

        public Decorate(World world, Random rand, BlockPos pos, EventType type)
        {
            super(world, rand, pos);
            this.type = type;
            this.totalAmount = null;
            this.modifiedAmount = null;
        }

        public Decorate(World world, Random rand, BlockPos pos, EventType type, int amount)
        {
            super(world, rand, pos);
            this.type = type;
            this.totalAmount = amount;
            this.modifiedAmount = amount;
        }

        public static class Generator extends Decorate
        {
            public final WorldGenerator generator;
            public final Position position;

            public enum Position
            {
                SURFACE,
                UNDERGROUND,
                AIR,
                ANYWHERE,
                OTHER
            }

            public Generator(World world, Random rand, BlockPos pos, EventType type, int amount, WorldGenerator generator, Position position)
            {
                super(world, rand, pos, type, amount);
                this.generator = generator;
                this.position = position;
            }
        }
    }
}
