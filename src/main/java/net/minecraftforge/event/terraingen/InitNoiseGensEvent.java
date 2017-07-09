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

import net.minecraft.world.World;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.NoiseGeneratorSimplex;
import net.minecraftforge.event.world.WorldEvent;

public class InitNoiseGensEvent<T extends InitNoiseGensEvent.Context> extends WorldEvent
{
    private final Random rand;
    private final T original;
    private T newValues;

    @SuppressWarnings("unchecked")
    public InitNoiseGensEvent(World world, Random rand, T original)
    {
        super(world);
        this.rand = rand;
        this.original = original;
        this.newValues = (T)original.clone();
    }
    public Random getRandom() { return this.rand; }
    public T getOriginal() { return this.original; }
    public T getNewValues() { return this.newValues; }

    public static class Context
    {
        private NoiseGeneratorOctaves lperlin1;
        private NoiseGeneratorOctaves lperlin2;
        private NoiseGeneratorOctaves perlin;
        private NoiseGeneratorOctaves scale;
        private NoiseGeneratorOctaves depth;
        public Context(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2, NoiseGeneratorOctaves perlin,
                       NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth)
        {
            this.lperlin1 = lperlin1;
            this.lperlin2 = lperlin2;
            this.perlin = perlin;
            this.scale = scale;
            this.depth = depth;
        }
        public NoiseGeneratorOctaves getLPerlin1() { return lperlin1; }
        public NoiseGeneratorOctaves getLPerlin2() { return lperlin2; }
        public NoiseGeneratorOctaves getPerlin()   { return perlin;   }
        public NoiseGeneratorOctaves getScale()    { return scale;    }
        public NoiseGeneratorOctaves getDepth()    { return depth;    }
        public void setLPerlin1(NoiseGeneratorOctaves value) { this.lperlin1 = value; }
        public void getLPerlin2(NoiseGeneratorOctaves value) { this.lperlin2 = value; }
        public void getPerlin  (NoiseGeneratorOctaves value) { this.perlin   = value; }
        public void getScale   (NoiseGeneratorOctaves value) { this.scale    = value; }
        public void getDepth   (NoiseGeneratorOctaves value) { this.depth    = value; }

        @Override
        public Context clone(){ return new Context(lperlin1, lperlin2, perlin, scale, depth); }
    }

    public static class ContextOverworld extends Context
    {
        private NoiseGeneratorPerlin  height;
        private NoiseGeneratorOctaves forest;

        public ContextOverworld(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2, NoiseGeneratorOctaves perlin,
                NoiseGeneratorPerlin height, NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth, NoiseGeneratorOctaves forest)
        {
            super(lperlin1, lperlin2, perlin, scale, depth);
            this.height = height;
            this.forest = forest;
        }
        @Override
        public ContextOverworld clone() { return new ContextOverworld(getLPerlin1(), getLPerlin2(), getPerlin(), height, getScale(), getDepth(), forest); }
        public NoiseGeneratorPerlin  getHeight()   { return height;   }
        public NoiseGeneratorOctaves getForest()   { return forest;   }

        public void getHeight  (NoiseGeneratorPerlin  value) { this.height   = value; }
        public void getForest  (NoiseGeneratorOctaves value) { this.forest   = value; }
    }

    public static class ContextEnd extends Context
    {
        private NoiseGeneratorSimplex island;
        public ContextEnd(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2, NoiseGeneratorOctaves perlin,
                NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth, NoiseGeneratorSimplex island)
        {
            super(lperlin1, lperlin2, perlin, scale, depth);
            this.island = island;
        }
        @Override
        public ContextEnd clone() { return new ContextEnd(getLPerlin1(), getLPerlin2(), getPerlin(), getScale(), getDepth(), island); }
        public NoiseGeneratorSimplex getIsland() { return island;   }
        public void getIsland  (NoiseGeneratorSimplex value) { this.island = value; }
    }


    public static class ContextHell extends Context
    {
        private NoiseGeneratorOctaves perlin2;
        private NoiseGeneratorOctaves perlin3;

        public ContextHell(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2, NoiseGeneratorOctaves perlin,
                NoiseGeneratorOctaves perlin2, NoiseGeneratorOctaves perlin3, NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth)
        {
            super(lperlin1, lperlin2, perlin, scale, depth);
            this.perlin2 = perlin2;
            this.perlin3 = perlin3;
        }
        @Override
        public ContextHell clone() { return new ContextHell(getLPerlin1(), getLPerlin2(), getPerlin(), perlin2, perlin3, getScale(), getDepth()); }
        public NoiseGeneratorOctaves getPerlin2() { return perlin2;  }
        public NoiseGeneratorOctaves getPerlin3() { return perlin3;  }
        public void getPerlin2 (NoiseGeneratorOctaves value) { this.perlin2 = value; }
        public void getPerlin3 (NoiseGeneratorOctaves value) { this.perlin3 = value; }
    }
}