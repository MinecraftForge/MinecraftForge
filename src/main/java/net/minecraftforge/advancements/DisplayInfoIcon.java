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

package net.minecraftforge.advancements;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraftforge.common.util.Constants.NBT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

/**
 * Used for rendering a texture as an advancement icon instead of providing an item.
 */
public class DisplayInfoIcon
{
    /*
     *  === USAGE ===
     * 
     * "display" : {
     *     "icon" : {
     *          "item" : "resource location of working item that Item#isDamageable() or Item#shouldSyncTag()",
     *          "nbt" : "{forgeAdvIcon:{ <list of properties, see block below> }}"
     *     } 
     * }
     * 
     * rendered texture size (properties asW asH) is capped at 26 pixels but should be smaller than 22 if an icon
     * use asW=asH=26 and offX=offY=-5 if you want to replace the default background, does affect announcement toast and tab icon (if root advanc.)
     * see minecraft:textures/gui/advancements/widgets.png for actual size of advancement toast
     * 
     *  === PROPERTIES ===
     * 
     * json key | type             | def value | description
     * 
     * loc      | ResourceLocation | REQUIRED  | texture location, example loc:"modid:textures/advancements/test.png"
     * offX     | Integer          | 0         | translate x before drawing
     * offY     | Integer          | 0         | translate y before drawing
     * w        | Integer          | 16        | texture width
     * h        | Integer          | 16        | texture height
     * texOffX  | Float            | 0.0       | inner texture x offset (aka u)
     * texOffY  | Float            | 0.0       | inner texture y offset (aka v)
     * asW      | Integer          | 16        | map texture width to
     * asH      | Integer          | 16        | map texture height to
     */

    // taken from advancement source code, advancement toast is 26x26 px
    private static final int MAX_SIZE = 26;
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NBT_KEY = "forgeAdvIcon";
    private static final String NBT_VALIDATED = "validated";
    private static final String NBT_DATA_LOCATION = "loc";
    private static final String NBT_DATA_DRAW_OFFSET_X = "offX";
    private static final String NBT_DATA_DRAW_OFFSET_Y = "offY";
    private static final String NBT_DATA_DRAW_WIDTH = "w";
    private static final String NBT_DATA_DRAW_HEIGHT = "h";
    private static final String NBT_DATA_TEXTURE_OFFSET_X = "texOffX";
    private static final String NBT_DATA_TEXTURE_OFFSET_Y = "texOffY";
    private static final String NBT_DATA_DRAW_AS_WIDTH = "asW";
    private static final String NBT_DATA_DRAW_AS_HEIGHT = "asH";

    /**
     * Validates provided properties and sets missing properties to default values.
     */
    public static void validateAndSetupForgeIcon(final ItemStack itemStack)
    {
        if (isForgeIcon(itemStack))
        {
            final CompoundNBT iconNBT = itemStack.getTag().getCompound(NBT_KEY);

            if (!(itemStack.getItem().shouldSyncTag() || itemStack.getItem().isDamageable()))
            {
                LOGGER.error("Invalid item \"{}\" for forge advancement icon, nbt: {}", itemStack.getItem().getRegistryName(), iconNBT.toString());
                iconNBT.putBoolean(NBT_VALIDATED, false);
                return;
            }

            if (validateTextureLocation(iconNBT, NBT_DATA_LOCATION) &&
                validateInteger(iconNBT, NBT_DATA_DRAW_OFFSET_X, -MAX_SIZE, MAX_SIZE, 0) &&
                validateInteger(iconNBT, NBT_DATA_DRAW_OFFSET_Y, -MAX_SIZE, MAX_SIZE, 0) &&
                validateInteger(iconNBT, NBT_DATA_DRAW_WIDTH, 1, Integer.MAX_VALUE, 16) &&
                validateInteger(iconNBT, NBT_DATA_DRAW_HEIGHT, 1, Integer.MAX_VALUE, 16) &&
                validateFloat(iconNBT, NBT_DATA_TEXTURE_OFFSET_X, 0.0f, Float.MAX_VALUE, 0.0f) &&
                validateFloat(iconNBT, NBT_DATA_TEXTURE_OFFSET_Y, 0.0f, Float.MAX_VALUE, 0.0f) &&
                validateInteger(iconNBT, NBT_DATA_DRAW_AS_WIDTH, 1, MAX_SIZE, 16) &&
                validateInteger(iconNBT, NBT_DATA_DRAW_AS_HEIGHT, 1, MAX_SIZE, 16))
            {
                iconNBT.putBoolean(NBT_VALIDATED, false);
                return;
            }

            iconNBT.putBoolean(NBT_VALIDATED, true);
        }
    }

    /**
     * Tries to render given itemStack as an advancement icon if itemstack is validated.
     * Logic copied from {@link net.minecraft.client.renderer.ItemRenderer#renderItemModelIntoGUI(ItemStack, int, int, IBakedModel)}.
     * 
     * @return true if forge icon item, false if not or provided resource location is not parsable
     */
    public static boolean renderForgeIcon(final ItemStack itemStack, final int x, final int y)
    {
        if (!isForgeIcon(itemStack))
        {
            return false;
        }

        CompoundNBT iconNBT = itemStack.getTag().getCompound(NBT_KEY);

        if (!iconNBT.getBoolean(NBT_VALIDATED))
        {
            // falls back to render the default icon
            return false;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(iconNBT.getString(NBT_DATA_LOCATION)));
        AbstractGui.blit(x + iconNBT.getInt(NBT_DATA_DRAW_OFFSET_X), y + iconNBT.getInt(NBT_DATA_DRAW_OFFSET_Y),
            iconNBT.getFloat(NBT_DATA_TEXTURE_OFFSET_X), iconNBT.getFloat(NBT_DATA_TEXTURE_OFFSET_Y),
            iconNBT.getInt(NBT_DATA_DRAW_WIDTH), iconNBT.getInt(NBT_DATA_DRAW_HEIGHT),
            iconNBT.getInt(NBT_DATA_DRAW_AS_WIDTH), iconNBT.getInt(NBT_DATA_DRAW_AS_HEIGHT));
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.disableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        return true;
    }

    /**
     * Checks if given itemStack has nbt compound tag named according to value of NBT_KEY
     */
    public static boolean isForgeIcon(final ItemStack itemStack)
    {
        return itemStack.hasTag() && itemStack.getTag().contains(NBT_KEY, NBT.TAG_COMPOUND);
    }

    /**
     * @return true if key passed the check, false if not
     */
    private static boolean validateTextureLocation(CompoundNBT nbt, String nbtKey)
    {
        if (!nbt.contains(nbtKey))
        {
            LOGGER.error("No texture location \"{}\" found for forge advancement icon, nbt: {}", nbtKey, nbt.toString());
            return false;
        }

        final String textureLoc = nbt.getString(nbtKey);

        try
        {
            new ResourceLocation(textureLoc);
        }
        catch (ResourceLocationException e)
        {
            LOGGER.error("Invalid texture location \"" + nbtKey + "\":\"" + textureLoc + "\" for forge advancement icon, nbt: " + nbt.toString(), e);
            return false;
        }

        return true;
    }

    /**
     * @return true if key passed the check, false if not
     */
    private static boolean validateInteger(CompoundNBT nbt, String nbtKey, int minValue, int maxValue, int defaultValue)
    {
        if (!nbt.contains(nbtKey))
        {
            nbt.putInt(nbtKey, defaultValue);
            return true;
        }

        final int nbtValue = nbt.getInt(nbtKey);
        
        if (nbtValue < minValue || nbtValue > maxValue)
        {
            LOGGER.error("Wrong \"{}\" for forge advancement icon, allowed range: [{} | {}], nbt: {}", nbtKey, minValue, maxValue, nbt.toString());
            return false;
        }
        return true;
    }

    /**
     * @return true if key passed the check, false if not
     */
    private static boolean validateFloat(CompoundNBT nbt, String nbtKey, float minValue, float maxValue, float defaultValue)
    {
        if (!nbt.contains(nbtKey))
        {
            nbt.putFloat(nbtKey, defaultValue);
            return true;
        }

        final float nbtValue = nbt.getFloat(nbtKey);
        
        if (nbtValue < minValue || nbtValue > maxValue)
        {
            LOGGER.error("Wrong \"{}\" for forge advancement icon, allowed range: [{} | {}], nbt: {}", nbtKey, minValue, maxValue, nbt.toString());
            return false;
        }
        return true;
    }
}
