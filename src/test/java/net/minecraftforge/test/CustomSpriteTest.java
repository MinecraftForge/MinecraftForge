package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Test for {@link TextureStitchEvent.Pre}.
 */
@Mod(modid = CustomSpriteTest.MOD_ID, name = CustomSpriteTest.NAME, version = "1.0")
public class CustomSpriteTest
{
    static final String MOD_ID = "custom_sprite_test";
    static final String NAME = "Custom sprite test";

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        Block block = new Block(Material.WOOD).setRegistryName(MOD_ID, "custom_sprite_block").setCreativeTab(CreativeTabs.MISC);
        GameRegistry.register(block);
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event)
    {
        DelegateSprite bottom = DelegateSprite.make("bottom", new ResourceLocation("textures/blocks/diamond_block.png"));
        DelegateSprite top = DelegateSprite.make("top", new ResourceLocation("textures/blocks/tnt_side.png"));

        TextureMap textureMap = event.getMap();
        textureMap.setTextureEntry(bottom);
        textureMap.setTextureEntry(top);
    }

    static final class DelegateSprite extends TextureAtlasSprite
    {
        final ResourceLocation delegate;

        private DelegateSprite(ResourceLocation loc, ResourceLocation delegate)
        {
            super(loc.toString());
            this.delegate = delegate;
        }

        static DelegateSprite make(String name, ResourceLocation delegate)
        {
            return new DelegateSprite(new ResourceLocation(MOD_ID, name), delegate);
        }

        @Override
        public boolean hasCustomLoader(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location)
        {
            return true;
        }

        @Override
        public boolean load(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location)
        {
            BufferedImage image;
            try
            {
                IResource resource = manager.getResource(delegate);
                image = ImageIO.read(resource.getInputStream());
            }
            catch (IOException ioe)
            {
                FMLLog.getLogger().error("Could not find resource", ioe);
                return true;
            }
            this.width = image.getWidth();
            this.height = image.getHeight();
            int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            pixels[0] = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels[0], 0, image.getWidth());
            this.clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return false;
        }
    }
}