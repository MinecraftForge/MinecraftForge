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

package net.minecraftforge.client;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IItemRenderer;

public class MinecraftForgeClient
{
	private static final HashMap<Item, IItemRenderer> customItemRenderers = new HashMap<Item, IItemRenderer>();
	
    public static int getRenderPass()
    {
        return ForgeHooksClient.renderPass;
    }

    public static BlockRenderLayer getRenderLayer()
    {
        return ForgeHooksClient.renderLayer.get();
    }

    /**
     * returns the Locale set by the player in Minecraft.
     * Useful for creating string and number formatters.
     */
    public static Locale getLocale()
    {
        return Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale();
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

    private static final LoadingCache<Pair<World, BlockPos>, ChunkCache> regionCache = CacheBuilder.newBuilder()
        .maximumSize(500)
        .concurrencyLevel(5)
        .expireAfterAccess(1, TimeUnit.SECONDS)
        .build(new CacheLoader<Pair<World, BlockPos>, ChunkCache>()
        {
            @Override
            public ChunkCache load(Pair<World, BlockPos> key)
            {
                return new ChunkCache(key.getLeft(), key.getRight().add(-1, -1, -1), key.getRight().add(16, 16, 16), 1);
            }
        });

    public static void onRebuildChunk(World world, BlockPos position, ChunkCache cache)
    {
        regionCache.put(Pair.of(world, position), cache);
    }

    public static ChunkCache getRegionRenderCache(World world, BlockPos pos)
    {
        int x = pos.getX() & ~0xF;
        int y = pos.getY() & ~0xF;
        int z = pos.getZ() & ~0xF;
        return regionCache.getUnchecked(Pair.of(world, new BlockPos(x, y, z)));
    }

    public static void clearRenderCache()
    {
        regionCache.invalidateAll();
        regionCache.cleanUp();
    }
    
    /**
     * Binds a custom item renderer to the specified item. When rendered, this custom renderer will be used.
     * @param item The item to bind the renderer to.
     * @param renderer The renderer for this item.
     * @throws IllegalArgumentException If the item is null, the renderer is null, or the item already has a custom renderer.
     */
    public static void registerItemRenderer(Item item, IItemRenderer renderer) {
    	if(item == null) {
    		throw new IllegalArgumentException("Item cannot be null!");
    	}
    	if(renderer == null) {
    		throw new IllegalArgumentException("Renderer cannot be null for item " + item.getRegistryName());
    	}
    	if(customItemRenderers.containsKey(item)) {
    		throw new IllegalArgumentException("Item " + item.getRegistryName() + " already has a custom item renderer!");
    	}
    	customItemRenderers.put(item, renderer);
    }
    
    /**
     * Returns the custom item renderer for the given item.
     * @param item The item to get the associated renderer for.
     * @return The custom renderer if this item has one, null otherwise.
     */
    @Nullable
    public static IItemRenderer getCustomItemRenderer(Item item) {
    	if(item != null || customItemRenderers.containsKey(item)) {
    		return customItemRenderers.get(item);
    	}
    	return null;
    }
}
