package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Test for {@link TextureStitchEvent.Pre}.
 */
@Mod(modid = CustomSpriteTest.MOD_ID, name = CustomSpriteTest.NAME)
public class CustomSpriteTest
{
    static final String MOD_ID = "custom_sprite_test";
    static final String NAME = "Custom sprite test";

    public void init(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
        Block block = new Block(Material.WOOD).setRegistryName(MOD_ID, "custom_sprite_block").setCreativeTab(CreativeTabs.MISC);
        GameRegistry.register(block);
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event)
    {
        event.getMap().setTextureEntry(DelegateSprite.make("bottom", "blocks/diamond_block"));
        event.getMap().setTextureEntry(DelegateSprite.make("top", "blocks/tnt_side"));
    }

    static final class DelegateSprite extends TextureAtlasSprite
    {
        final ResourceLocation location;
        final ResourceLocation delegate;

        private DelegateSprite(ResourceLocation loc, ResourceLocation delegate)
        {
            super(loc.toString());
            this.location = loc;
            this.delegate = delegate;
        }

        static DelegateSprite make(String name, String delegate)
        {
            return new DelegateSprite(new ResourceLocation(MOD_ID, name), new ResourceLocation("minecraft", delegate));
        }

        @Override
        public boolean hasCustomLoader(IResourceManager manager, ResourceLocation location)
        {
            return location.equals(this.location);
        }

        @Override
        public boolean load(IResourceManager manager, ResourceLocation location)
        {
            BufferedImage image;
            try
            {
                image = ImageIO.read(manager.getResource(delegate).getInputStream());
            }
            catch (IOException ioe)
            {
                return false;
            }
            int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            pixels[0] = new int[image.getWidth() * image.getHeight()];
            image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels[0], 0, image.getWidth());
            this.clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return false;
        }
    }
}
