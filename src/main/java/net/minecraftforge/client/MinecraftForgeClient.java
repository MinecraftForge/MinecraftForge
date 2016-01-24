/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.client;

import java.util.BitSet;
import java.util.concurrent.TimeUnit;

import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class MinecraftForgeClient
{
    public static int getRenderPass()
    {
        return ForgeHooksClient.renderPass;
    }

    public static EnumWorldBlockLayer getRenderLayer()
    {
        return ForgeHooksClient.renderLayer.get();
    }

    private static BitSet stencilBits = new BitSet(8);
    static
    {
        stencilBits.set(0,8);
    }

    /**
     * Reserve a stencil bit for use in rendering
     *
     * Note: you must check the Framebuffer you are working with to
     * determine if stencil bits are enabled on it before use.
     *
     * @return A bit or -1 if no further stencil bits are available
     */
    public static int reserveStencilBit()
    {
        int bit = stencilBits.nextSetBit(0);
        if (bit >= 0)
        {
            stencilBits.clear(bit);
        }
        return bit;
    }

    /**
     * Release the stencil bit for other use
     *
     * @param bit The bit from {@link #reserveStencilBit()}
     */
    public static void releaseStencilBit(int bit)
    {
        if (bit >= 0 && bit < stencilBits.length())
        {
            stencilBits.set(bit);
        }
    }

    private static final LoadingCache<Pair<World, BlockPos>, RegionRenderCache> regionCache = CacheBuilder.newBuilder()
        .maximumSize(500)
        .concurrencyLevel(5)
        .expireAfterAccess(1, TimeUnit.SECONDS)
        .build(new CacheLoader<Pair<World, BlockPos>, RegionRenderCache>()
        {
            public RegionRenderCache load(Pair<World, BlockPos> key) throws Exception
            {
                return new RegionRenderCache(key.getLeft(), key.getRight().add(-1, -1, -1), key.getRight().add(16, 16, 16), 1);
            }
        });

    public static void onRebuildChunk(World world, BlockPos position, RegionRenderCache cache)
    {
        regionCache.put(Pair.of(world, position), cache);
    }

    public static RegionRenderCache getRegionRenderCache(World world, BlockPos pos)
    {
        int x = pos.getX() & ~0xF;
        int y = pos.getY() & ~0xF;
        int z = pos.getZ() & ~0xF;
        return regionCache.getUnchecked(Pair.of(world, new BlockPos(x, y, z)));
    }
}
