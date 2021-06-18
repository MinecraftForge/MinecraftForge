package net.minecraftforge.debug.client;

import java.util.Random;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomTASTest.MOD_ID)
public class CustomTASTest
{

    private static final boolean enabled = true;
    static final String MOD_ID = "custom_tas_test";
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final RegistryObject<Item> TEST_ITEM = ITEMS.register("test_item", () -> new Item(new Item.Properties()));

    public CustomTASTest()
    {
        if (enabled)
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
        public TextureAtlasSprite load(AtlasTexture atlas, IResourceManager resourceManager, TextureAtlasSprite.Info textureInfo, IResource resource, int atlasWidth, int atlasHeight, int spriteX, int spriteY, int mipmapLevel, NativeImage image)
        {
            return new TextureAtlasSprite(
                    atlas, textureInfo, mipmapLevel, atlasWidth, atlasHeight, spriteX, spriteY, image
            )
            {

                private final Random random = new Random();

                @Override
                public boolean isAnimation()
                {
                    return true;
                }

                @Override
                public void cycleFrames()
                {
                    this.mainImage[0].fillRect(0, 0, 16, 16, 0xFF000000 | random.nextInt(0xFFFFFF));
                    this.uploadFirstFrame();
                }
            };
        }
    }

}
