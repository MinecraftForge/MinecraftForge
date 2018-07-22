/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.function.Function;

/**
 * Test for {@link TextureStitchEvent.Pre}.
 */
@Mod(modid = CustomTextureAtlasSpriteTest.MOD_ID, name = CustomTextureAtlasSpriteTest.NAME, version = "1.0", clientSideOnly = true)
public class CustomTextureAtlasSpriteTest
{
    static final String MOD_ID = "custom_sprite_test";
    static final String NAME = "Custom sprite test";
    private static Logger logger;


    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().register(new Block(Material.WOOD).setRegistryName(MOD_ID, "custom_sprite_block").setCreativeTab(CreativeTabs.MISC));
        }
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void textureStitch(TextureStitchEvent.Pre event)
    {
        DelegateSprite bottom = DelegateSprite.make("bottom", new ResourceLocation("blocks/diamond_block"));
        DelegateSprite top = DelegateSprite.make("top", new ResourceLocation("blocks/tnt_side"));

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
        public Collection<ResourceLocation> getDependencies()
        {
            return ImmutableList.of(delegate);
        }

        @Override
        public boolean load(@Nonnull IResourceManager manager, @Nonnull ResourceLocation location, @Nonnull Function<ResourceLocation, TextureAtlasSprite> textureGetter)
        {
            final TextureAtlasSprite sprite = textureGetter.apply(delegate);
            width = sprite.getIconWidth();
            height = sprite.getIconHeight();
            final int[][] oldPixels = sprite.getFrameTextureData(0);
            final int[][] pixels = new int[Minecraft.getMinecraft().gameSettings.mipmapLevels + 1][];
            pixels[0] = new int[width * height];
            for (int p = 0; p < width * height; p++) {
                pixels[0][p] = oldPixels[0][p] >> 8;
            }
            this.clearFramesTextureData();
            this.framesTextureData.add(pixels);
            return false;
        }
    }
}
