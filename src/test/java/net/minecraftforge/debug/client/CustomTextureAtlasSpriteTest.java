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

package net.minecraftforge.debug.client;

import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.PngSizeInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Function;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Test for {@link TextureStitchEvent.Pre}.
 */

@Mod(CustomTextureAtlasSpriteTest.MOD_ID)
@Mod.EventBusSubscriber(modid = CustomTextureAtlasSpriteTest.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomTextureAtlasSpriteTest
{
    public static final String MOD_ID = "custom_sprite_test";
    private static final String BLOCK_ID = "custom_sprite_block";

    @ObjectHolder(MOD_ID + ":" + BLOCK_ID)
    private static Block spriteBlock;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new Block(Block.Properties.create(Material.WOOD).noDrops()).setRegistryName(MOD_ID, BLOCK_ID));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BlockItem(spriteBlock, new Item.Properties().group(ItemGroup.MISC)).setRegistryName(MOD_ID, BLOCK_ID));
    }

    @SubscribeEvent
    public static void textureStitch(TextureStitchEvent.Pre event)
    {
        AtlasTexture map = event.getMap();
        if (map == Minecraft.getInstance().getTextureMap())
        {
            event.addSprite(resourceManager -> DelegateSprite.make(createSpriteId("bottom"), map, new ResourceLocation("block/diamond_block"), resourceManager));
            event.addSprite(resourceManager -> DelegateSprite.make(createSpriteId("top"), map, new ResourceLocation("block/tnt_side"), resourceManager));
        }
    }

    private static class DelegateSprite extends TextureAtlasSprite
    {
        private final ResourceLocation delegate;

        private DelegateSprite(ResourceLocation loc, ResourceLocation delegate, int width, int height)
        {
            super(loc, width, height);
            this.delegate = delegate;
        }

        static DelegateSprite make(ResourceLocation id, AtlasTexture map, ResourceLocation delegate, IResourceManager manager)
        {
            ResourceLocation resource = map.getSpritePath(delegate);
            try (IResource iresource = manager.getResource(resource))
            {
                PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource.toString(), iresource.getInputStream());
                return new DelegateSprite(id, delegate, pngsizeinfo.width, pngsizeinfo.height);
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Collection<ResourceLocation> getDependencies()
        {
            return ImmutableList.of(delegate);
        }

        @Override
        public void load(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location, int mipmapLevels, @Nonnull Function<ResourceLocation, CompletableFuture<TextureAtlasSprite>> textureGetter)
        {
            CompletableFuture<TextureAtlasSprite> spriteFuture = textureGetter.apply(delegate);
            TextureAtlasSprite sprite = spriteFuture.join();
            NativeImage source = sprite.getImage(0);
            NativeImage image = new NativeImage(source.getFormat(), width, height, true);
            for (int i = 0; i < width; i++)
            {
                for (int j = 0; j < height; j++)
                {
                    image.setPixelRGBA(i, j, ~source.getPixelRGBA(i, j) | 0xFF000000);
                }
            }

            frames = new NativeImage[mipmapLevels];
            frames[0] = image;
        }
    }

    private static ResourceLocation createSpriteId(String name)
    {
        return new ResourceLocation(MOD_ID, name);
    }
}
