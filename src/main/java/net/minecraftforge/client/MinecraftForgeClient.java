/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.mojang.datafixers.util.Either;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;

public class MinecraftForgeClient
{
    public static RenderType getRenderLayer()
    {
        return ForgeHooksClient.renderLayer.get();
    }

    /**
     * returns the Locale set by the player in Minecraft.
     * Useful for creating string and number formatters.
     */
    public static Locale getLocale()
    {
        return Minecraft.getInstance().getLanguageManager().getSelected().getJavaLocale();
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

    private static final LoadingCache<Pair<Level, BlockPos>, Optional<RenderChunkRegion>> regionCache = CacheBuilder.newBuilder()
        .maximumSize(500)
        .concurrencyLevel(5)
        .expireAfterAccess(1, TimeUnit.SECONDS)
        .build(new CacheLoader<Pair<Level, BlockPos>, Optional<RenderChunkRegion>>()
        {
            @Override
            public Optional<RenderChunkRegion> load(Pair<Level, BlockPos> key)
            {
                return Optional.ofNullable(RenderChunkRegion.createIfNotEmpty(key.getLeft(), key.getRight().offset(-1, -1, -1), key.getRight().offset(16, 16, 16), 1));
            }
        });

    public static void onRebuildChunk(Level world, BlockPos position, RenderChunkRegion cache)
    {
        if (cache == null)
            regionCache.invalidate(Pair.of(world, position));
        else
            regionCache.put(Pair.of(world, position), Optional.of(cache));
    }

    @Nullable
    public static RenderChunkRegion getRegionRenderCache(Level world, BlockPos pos)
    {
        return getRegionRenderCacheOptional(world, pos).orElse(null);
    }

    public static Optional<RenderChunkRegion> getRegionRenderCacheOptional(Level world, BlockPos pos)
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

    private static HashMap<ResourceLocation, Supplier<NativeImage>> bufferedImageSuppliers = new HashMap<ResourceLocation, Supplier<NativeImage>>();
    public static void registerImageLayerSupplier(ResourceLocation resourceLocation, Supplier<NativeImage> supplier)
    {
        bufferedImageSuppliers.put(resourceLocation, supplier);
    }

    @Nonnull
    public static NativeImage getImageLayer(ResourceLocation resourceLocation, ResourceManager resourceManager) throws IOException
    {
        Supplier<NativeImage> supplier = bufferedImageSuppliers.get(resourceLocation);
        if (supplier != null)
            return supplier.get();

        Resource iresource1 = resourceManager.getResource(resourceLocation);
        return NativeImage.read(iresource1.getInputStream());
    }

    private static final Map<ResourceLocation, ITextureAtlasSpriteLoader> textureAtlasSpriteLoaders = new ConcurrentHashMap<>();

    /**
     * Register a custom ITextureAtlasSprite loader. Call this method during {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent}.
     */
    public static void registerTextureAtlasSpriteLoader(ResourceLocation name, ITextureAtlasSpriteLoader loader)
    {
        textureAtlasSpriteLoaders.put(name, loader);
    }

    @Nullable
    public static ITextureAtlasSpriteLoader getTextureAtlasSpriteLoader(ResourceLocation name)
    {
        return textureAtlasSpriteLoaders.get(name);
    }

    private static final Map<Class<? extends TooltipComponent>, Function<TooltipComponent, ClientTooltipComponent>> tooltipComponentFactories = new ConcurrentHashMap<>();

    /**
     * Register a factory for ClientTooltipComponents.
     * @param cls the class for the component
     * @param factory the factory for the ClientTooltipComponent
     */
    @SuppressWarnings("unchecked")
    public static <T extends TooltipComponent> void registerTooltipComponentFactory(Class<T> cls, Function<? super T, ? extends ClientTooltipComponent> factory)
    {
        tooltipComponentFactories.put(cls, (Function<TooltipComponent, ClientTooltipComponent>) factory);
    }

    @Nullable
    public static ClientTooltipComponent getClientTooltipComponent(TooltipComponent component)
    {
        var factory = tooltipComponentFactories.get(component.getClass());
        return factory == null ? null : factory.apply(component);
    }

    public static List<ClientTooltipComponent> gatherTooltipComponents(List<? extends FormattedText> textElements, int mouseX, int mouseY, int screenWidth, int screenHeight, Font font)
    {
        return gatherTooltipComponents(textElements, Optional.empty(), mouseX, mouseY, screenWidth, screenHeight, font);
    }
    public static List<ClientTooltipComponent> gatherTooltipComponents(List<? extends FormattedText> textElements, Optional<TooltipComponent> itemComponent, int mouseX, int mouseY, int screenWidth, int screenHeight, Font font)
    {
        List<Either<FormattedText, TooltipComponent>> elements = textElements.stream()
                .map((Function<FormattedText, Either<FormattedText, TooltipComponent>>) Either::left)
                .collect(Collectors.toCollection(ArrayList::new));
        itemComponent.ifPresent(c -> elements.add(Either.right(c)));

        var event = new RenderTooltipEvent.GatherComponents(screenWidth, screenHeight, elements, -1);
        MinecraftForge.EVENT_BUS.post(event);

        // text wrapping
        int tooltipTextWidth = event.getTooltipElements().stream()
                .mapToInt(either -> either.map(font::width, component -> 0))
                .max()
                .orElse(0);

        boolean needsWrap = false;

        int titleLinesCount = 1;
        int tooltipX = mouseX + 12;
        if (tooltipX + tooltipTextWidth + 4 > screenWidth)
        {
            tooltipX = mouseX - 16 - tooltipTextWidth;
            if (tooltipX < 4) // if the tooltip doesn't fit on the screen
            {
                if (mouseX > screenWidth / 2)
                    tooltipTextWidth = mouseX - 12 - 8;
                else
                    tooltipTextWidth = screenWidth - 16 - mouseX;
                needsWrap = true;
            }
        }

        if (event.getMaxWidth() > 0 && tooltipTextWidth > event.getMaxWidth())
        {
            tooltipTextWidth = event.getMaxWidth();
            needsWrap = true;
        }

        int tooltipTextWidthF = tooltipTextWidth;
        if (needsWrap)
        {
            return elements.stream()
                    .flatMap(either -> either.map(
                            text -> font.split(text, tooltipTextWidthF).stream().map(ClientTooltipComponent::create),
                            component -> Stream.of(ClientTooltipComponent.create(component))
                    ))
                    .toList();
        }
        else
        {
            return elements.stream()
                    .map(either -> either.map(
                            text -> ClientTooltipComponent.create(text instanceof Component ? ((Component) text).getVisualOrderText() : Language.getInstance().getVisualOrder(text)),
                            ClientTooltipComponent::create
                    ))
                    .toList();
        }
    }

}
