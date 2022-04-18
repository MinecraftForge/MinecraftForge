/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import java.io.IOException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
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
    public static RenderType getRenderType()
    {
        return ForgeHooksClient.renderType.get();
    }

    private static float partialTick;

    public static float getPartialTick() {
        return partialTick;
    }

    public static void setPartialTick(float partialTick) {
        MinecraftForgeClient.partialTick = partialTick;
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

}
