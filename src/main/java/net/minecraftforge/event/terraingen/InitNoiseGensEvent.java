/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.world.IWorld;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.event.world.WorldEvent;

public class InitNoiseGensEvent<T extends InitNoiseGensEvent.Context> extends WorldEvent
{
    private final Random rand;
    private final T original;
    private T newValues;

    @SuppressWarnings("unchecked")
    public InitNoiseGensEvent(IWorld world, Random rand, T original)
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

        public ContextOverworld(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2, NoiseGeneratorOctaves perlin,
                NoiseGeneratorPerlin height, NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth)
        {
            super(lperlin1, lperlin2, perlin, scale, depth);
            this.height = height;
        }
        @Override
        public ContextOverworld clone() { return new ContextOverworld(getLPerlin1(), getLPerlin2(), getPerlin(), height, getScale(), getDepth()); }
        public NoiseGeneratorPerlin  getHeight()   { return height;   }

        public void getHeight  (NoiseGeneratorPerlin  value) { this.height   = value; }
    }

    public static class ContextEnd extends Context
    {
        private NoiseGeneratorPerlin island;
        public ContextEnd(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2, NoiseGeneratorOctaves perlin,
                NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth, NoiseGeneratorPerlin field_205478_l)
        {
            super(lperlin1, lperlin2, perlin, scale, depth);
            this.island = field_205478_l;
        }
        @Override
        public ContextEnd clone() { return new ContextEnd(getLPerlin1(), getLPerlin2(), getPerlin(), getScale(), getDepth(), island); }
        public NoiseGeneratorPerlin getIsland() { return island;   }
        public void getIsland  (NoiseGeneratorPerlin value) { this.island = value; }
    }


    public static class ContextHell extends Context
    {
        private NoiseGeneratorOctaves perlin2;

        public ContextHell(NoiseGeneratorOctaves lperlin1, NoiseGeneratorOctaves lperlin2, NoiseGeneratorOctaves perlin,
                NoiseGeneratorOctaves perlin2, NoiseGeneratorOctaves scale, NoiseGeneratorOctaves depth)
        {
            super(lperlin1, lperlin2, perlin, scale, depth);
            this.perlin2 = perlin2;
        }
        @Override
        public ContextHell clone() { return new ContextHell(getLPerlin1(), getLPerlin2(), getPerlin(), perlin2, getScale(), getDepth()); }
        public NoiseGeneratorOctaves getPerlin2() { return perlin2;  }
        public void getPerlin2 (NoiseGeneratorOctaves value) { this.perlin2 = value; }
    }
}