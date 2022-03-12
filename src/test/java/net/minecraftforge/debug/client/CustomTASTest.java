/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import java.util.Random;

import javax.annotation.Nonnull;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomTASTest.MOD_ID)
public class CustomTASTest
{
    private static final boolean ENABLED = true;
    static final String MOD_ID = "custom_tas_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item", () -> new Item(new Item.Properties()));

    public CustomTASTest()
    {
        if (ENABLED)
        {
            if (FMLLoader.getDist().isClient())
            {
                FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
            }
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    private void clientSetup(FMLClientSetupEvent event)
    {
        MinecraftForgeClient.registerTextureAtlasSpriteLoader(new ResourceLocation(MOD_ID, "tas_loader"), new TasLoader());
    }

    private static class TasLoader implements ITextureAtlasSpriteLoader
    {
        @Override
        @Nonnull
        public TextureAtlasSprite load(TextureAtlas atlas, ResourceManager resourceManager, TextureAtlasSprite.Info textureInfo, Resource resource, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel, NativeImage image)
        {
            return new TextureAtlasSprite(
                    atlas, textureInfo, mipmapLevel, atlasWidth, atlasHeight, spriteX, spriteY, image
            )
            {
                private final Random random = new Random();

                @Override
                public Tickable getAnimationTicker()
                {
                    return this::tick;
                }

                private void tick()
                {
                    this.mainImage[0].fillRect(0, 0, 16, 16, 0xFF000000 | random.nextInt(0xFFFFFF));
                    this.uploadFirstFrame();
                }
            };
        }
    }

}
