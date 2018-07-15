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

package net.minecraftforge.debug.gameplay;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BannerTextures;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.BufferedImageLoadEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = DynamicBannerEventTest.MODID, name = "ForgeDebugDynamicBanner", version = DynamicBannerEventTest.VERSION, acceptableRemoteVersions = "*")
public class DynamicBannerEventTest
{
    public static final String MODID = "forgedebugdynamicbanner";
    public static final String VERSION = "1.0";

    public static final CreativeTabs bannerTab = new CreativeTabBanners("dynbanner.banners");

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        addBasicPattern("Y");
    }

    public static class EventHandler
    {
        @SideOnly(Side.CLIENT)
        @SubscribeEvent
        public void getBufferedImage(BufferedImageLoadEvent event)
        {
            if (!event.getResourceLocation().contains(MODID))
                return;

            BufferedImage baseImage = buildBackground();
            ColorModel cm = baseImage.getColorModel();
            BufferedImage copy = new BufferedImage(cm, baseImage.copyData(null), cm.isAlphaPremultiplied(), null);

            int width = 11;
            int height = 30;
            int startX = 5;
            int startZ = 5;

            for (int xx = startX; xx <= startX + width; xx++)
            {
                for (int zz = startZ; zz <= startZ + height; zz++)
                {
                    copy.setRGB(xx, zz, 0xFF000000); // Black
                }
            }

            event.setResultBufferedImage(copy);
        }
    }

    public static BannerPattern addBasicPattern(String name)
    {
        final Class<?>[] paramTypes = { String.class, String.class };
        final Object[] paramValues = { MODID + "_" + name, MODID + "." + name };
        return EnumHelper.addEnum(BannerPattern.class, name.toUpperCase(), paramTypes, paramValues);
    }

    @SideOnly(Side.CLIENT)
    private static BufferedImage buildBackground()
    {
        ResourceLocation originalBackground = BannerTextures.BANNER_BASE_TEXTURE;
        try (InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(originalBackground)
                .getInputStream())
        {
            return ImageIO.read(is);
        } catch (IOException exc)
        {
            throw new RuntimeException("Couldn't find or open the page background image.", exc);
        }
    }

    public static NBTTagList makePatternNBTList(BannerPattern pattern, EnumDyeColor color)
    {
        final NBTTagList patterns = new NBTTagList();
        final NBTTagCompound tag = new NBTTagCompound();
        tag.setString("Pattern", pattern.getHashname());
        tag.setInteger("Color", color.getDyeDamage());
        patterns.appendTag(tag);
        return patterns;
    }

    public static class CreativeTabBanners extends CreativeTabs
    {
        private static ItemStack DISPLAY = null;

        public CreativeTabBanners(String id)
        {
            super(id);
            this.setBackgroundImageName("item_search.png");
        }

        @Override
        public ItemStack getTabIconItem()
        {
            return this.getIconItemStack();
        }

        @Override
        public ItemStack getIconItemStack()
        {
            if (DISPLAY == null)
                DISPLAY = ItemBanner.makeBanner(EnumDyeColor.WHITE, makePatternNBTList(BannerPattern.CREEPER, EnumDyeColor.GREEN));

            return DISPLAY;
        }

        @Override
        public boolean hasSearchBar()
        {
            return true;
        }

        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> itemList)
        {
            super.displayAllRelevantItems(itemList);

            for (final BannerPattern pattern : BannerPattern.values())
                itemList.add(ItemBanner.makeBanner(EnumDyeColor.WHITE, makePatternNBTList(pattern, EnumDyeColor.BLACK)));
        }
    }
}
