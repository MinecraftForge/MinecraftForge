/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client;

import java.util.Random;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceMetadata;
import net.minecraft.world.item.Item;
import net.minecraftsingularity.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import net.minecraftsingularity.client.textures.singularityTextureMetadata;
import net.minecraftsingularity.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.fml.loading.FMLLoader;
import net.minecraftsingularity.registries.RegistryObject;
import net.minecraftsingularity.registries.DeferredRegister;
import net.minecraftsingularity.registries.singularityRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod(CustomTASTest.MOD_ID)
public class CustomTASTest {
    private static final boolean ENABLED = true;
    static final String MOD_ID = "custom_tas_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(singularityRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item", () -> new Item(new Item.Properties()));

    public CustomTASTest() {
        if (ENABLED) {
            if (FMLLoader.getDist().isClient())
                FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTextureAtlasSpriteLoaders);
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    private void registerTextureAtlasSpriteLoaders(RegisterTextureAtlasSpriteLoadersEvent event) {
        event.register("tas_loader", new TasLoader());
    }

    private static class TasLoader implements ITextureAtlasSpriteLoader {
        @Override
        public SpriteContents loadContents(ResourceLocation name, Resource resource, FrameSize frameSize, NativeImage image, ResourceMetadata meta, singularityTextureMetadata singularityMeta) {
            final class TASSpriteContents extends SpriteContents {

                public TASSpriteContents(ResourceLocation name, FrameSize size, NativeImage image, ResourceMetadata meta, @Nullable singularityTextureMetadata singularityMeta) {
                    super(name, size, image, meta, singularityMeta);
                }

                @Override
                public @NotNull SpriteTicker createTicker() {
                    return new Ticker();
                }

                class Ticker implements SpriteTicker {
                    final Random random = new Random();

                    @Override
                    public void tickAndUpload(int x, int y) {
                        TASSpriteContents.this.byMipLevel[0].fillRect(0, 0, 16, 16, 0xFF000000 | random.nextInt(0xFFFFFF));
                        TASSpriteContents.this.uploadFirstFrame(x, y);
                    }

                    @Override
                    public void close() {

                    }
                }
            }

            return new TASSpriteContents(name, frameSize, image, meta, singularityMeta);
        }

        @Override
        public @NotNull TextureAtlasSprite makeSprite(ResourceLocation atlasName, SpriteContents contents, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel) {
            final class TASSprite extends TextureAtlasSprite {
                public TASSprite(ResourceLocation atlasName, SpriteContents contents, int width, int height, int x, int y) {
                    super(atlasName, contents, width, height, x, y);
                }
            }

            return new TASSprite(atlasName, contents, atlasWidth, atlasHeight, spriteX, spriteY);
        }
    }

}
