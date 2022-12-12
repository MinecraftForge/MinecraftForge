/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import java.util.Random;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
                FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerTextureAtlasSpriteLoaders);
            }
            ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }

    private void registerTextureAtlasSpriteLoaders(RegisterTextureAtlasSpriteLoadersEvent event)
    {
        event.register("tas_loader", new TasLoader());
    }

    private static class TasLoader implements ITextureAtlasSpriteLoader
    {
        @Override
        public SpriteContents loadContents(ResourceLocation name, Resource resource, FrameSize frameSize, NativeImage image, AnimationMetadataSection animationMeta, ForgeTextureMetadata forgeMeta) {
            final class TASSpriteContents extends SpriteContents {

                public TASSpriteContents(ResourceLocation p_249787_, FrameSize p_251031_, NativeImage p_252131_, AnimationMetadataSection p_250432_, @Nullable ForgeTextureMetadata forgeMeta) {
                    super(p_249787_, p_251031_, p_252131_, p_250432_, forgeMeta);
                }

                @Override
                public @NotNull SpriteTicker createTicker() {
                    return new Ticker();
                }

                class Ticker implements SpriteTicker {

                    final Random random = new Random();

                    @Override
                    public void tickAndUpload(int p_248847_, int p_250486_) {
                        TASSpriteContents.this.byMipLevel[0].fillRect(0, 0, 16, 16, 0xFF000000 | random.nextInt(0xFFFFFF));
                        TASSpriteContents.this.uploadFirstFrame(p_248847_, p_250486_);
                    }

                    @Override
                    public void close() {

                    }
                }
            }

            return new TASSpriteContents(name, frameSize, image, animationMeta, forgeMeta);
        }

        @Override
        public @NotNull TextureAtlasSprite makeSprite(ResourceLocation atlasName, SpriteContents contents, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel) {
            final class TASSprite extends TextureAtlasSprite {

                public TASSprite(ResourceLocation p_250211_, SpriteContents p_248526_, int p_248950_, int p_249741_, int p_248672_, int p_248637_) {
                    super(p_250211_, p_248526_, p_248950_, p_249741_, p_248672_, p_248637_);
                }
            }

            return new TASSprite(atlasName, contents, atlasWidth, atlasHeight, spriteX, spriteY);
        }
    }

}
