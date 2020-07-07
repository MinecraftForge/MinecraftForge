package net.minecraftforge.debug.client;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomSpriteTest.MODID)
public class CustomSpriteTest
{
    public static final String MODID = "custom_sprite_test";
    private static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
    private static final RegistryObject<Block> CUSTOM_SPRITE_BLOCK = BLOCKS.register("custom_sprite_block", () -> new Block(Block.Properties.from(Blocks.COBBLESTONE)));
    private static final RegistryObject<Item> BLOCK_ITEM = ITEMS.register("custom_sprite_block", () -> new BlockItem(CUSTOM_SPRITE_BLOCK.get(), new Item.Properties().group(ItemGroup.SEARCH)));

    public CustomSpriteTest()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> bus.addListener(this::registerSprite));
    }

    private void registerSprite(TextureStitchEvent.Pre event)
    {
        if (!event.getMap().getTextureLocation().equals(AtlasTexture.LOCATION_BLOCKS_TEXTURE))
            return;

        event.getMap().addCustomSprite(TestSprite.INFO, TestSprite::new);
    }

    private static class TestSprite extends TextureAtlasSprite
    {
        private static final ResourceLocation NAME = new ResourceLocation(MODID, "test_sprite");
        private static final int SIZE = 16;
        private static final TextureAtlasSprite.Info INFO = new TextureAtlasSprite.Info(NAME, SIZE, SIZE,
                new AnimationMetadataSection(Lists.newArrayList(new AnimationFrame(0, -1)), SIZE, SIZE, 1, false));
        private static final LazyValue<NativeImage> IMAGE = new LazyValue<>(() ->
        {
            NativeImage image = new NativeImage(SIZE, SIZE, false);

            for(int x = 0; x < SIZE; x++)
                for(int y = 0; y < SIZE; y++)
                    if (x < 8 ^ y < 8)
                        image.setPixelRGBA(y, x, 0xFFFF0000);
                    else
                        image.setPixelRGBA(y, x, 0xFF0000FF);

            image.untrack();
            return image;
        });

        public TestSprite(AtlasTexture atlas, TextureAtlasSprite.Info info, int mipMapLevels, int atlasWidth, int atlasHeight, int x, int y)
        {
            super(atlas, info, mipMapLevels, atlasWidth, atlasHeight, x, y, IMAGE.getValue());
        }

        @Override
        public void close()
        {
            for(int i = 1; i < frames.length; i++)
                frames[i].close();
        }
    }
}
