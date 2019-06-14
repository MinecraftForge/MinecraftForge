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

package net.minecraftforge.debug.gameplay;

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
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// TODO 1.13: implement without java.awt
//@Mod(modid = DynamicBannerTest.MODID, name = "ForgeDebugDynamicBanner", version = DynamicBannerTest.VERSION, acceptableRemoteVersions = "*")
public class DynamicBannerTest
{
    private static final boolean ENABLE = false;
    public static final String MODID = "forgedebugdynamicbanner";
    public static final String VERSION = "1.0";

    public static CreativeTabs bannerTab;
    @SidedProxy
    public static CommonProxy proxy = null;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            BannerPattern pattern = addBasicPattern("Y");
            proxy.registerSupplier(new ResourceLocation("textures/entity/banner/" + pattern.getFileName() + ".png"));
            proxy.registerSupplier(new ResourceLocation("textures/entity/shield/" + pattern.getFileName() + ".png"));
            bannerTab = new CreativeTabBanners("dynbanner.banners");
        }
    }

    public static abstract class CommonProxy
    {
        public void registerSupplier(ResourceLocation location)
        {
        }
    }

    public static final class ServerProxy extends CommonProxy
    {
    }

    public static final class ClientProxy extends CommonProxy
    {
        @Override
        @SideOnly(Side.CLIENT)
        public void registerSupplier(ResourceLocation location)
        {
            MinecraftForgeClient.registerImageLayerSupplier(location, () -> {
                return createBufferedImage();
            });
        }
    }

    public static BannerPattern addBasicPattern(String name)
    {
        final Class<?>[] paramTypes = { String.class, String.class };
        final Object[] paramValues = { MODID + "_" + name, MODID + "." + name };
        return EnumHelper.addEnum(BannerPattern.class, name.toUpperCase(), paramTypes, paramValues);
    }

    @SideOnly(Side.CLIENT)
    private static BufferedImage createBufferedImage()
    {
        BufferedImage baseImage = buildBackground();

        int width = 11;
        int height = 30;
        int startX = 5;
        int startY = 5;

        for (int xx = startX; xx <= startX + width; xx++)
        {
            for (int yy = startY; yy <= startY + height; yy++)
            {
                baseImage.setRGB(xx, yy, 0xFF000000); // Black
            }
        }
        return baseImage;
    }

    @SideOnly(Side.CLIENT)
    private static BufferedImage buildBackground()
    {
        ResourceLocation originalBackground = BannerTextures.BANNER_BASE_TEXTURE;
        try (InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(originalBackground).getInputStream())
        {
            return ImageIO.read(is);
        }
        catch (IOException exc)
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
