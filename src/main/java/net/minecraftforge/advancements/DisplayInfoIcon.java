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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

/**
 * Used for rendering of a texture as an advancement icon instead of need of providing a proxy item.
 */
public class DisplayInfoIcon
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String NBT_KEY = "forgeAdvIcon";

    /**
     * Tries to render given itemStack as an advancement icon if itemstack passes through {@link #isForgeIcon()}.
     * 
     * @return true if forge icon item, false if not or provided resource location is not parsable
     */
    public static boolean renderForgeIcon(final ItemStack itemStack, final int x, final int y)
    {
        if(!isForgeIcon(itemStack))
        {
            return false;
        }

        final CompoundNBT iconCompound = itemStack.getTag().getCompound(NBT_KEY);
        final String texturePath = iconCompound.getString("location");
        final ResourceLocation textureLocation;

        try
        {
            textureLocation = new ResourceLocation(texturePath);
        }
        catch (ResourceLocationException e)
        {
            LOGGER.error("Invalid resource location for forge advancement icon: " + texturePath, e);
            // fall back to render the default icon
            return false;
        }

        // copied from net.minecraft.client.renderer.ItemRenderer.renderItemModelIntoGUI(ItemStack, int, int, IBakedModel)
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(textureLocation);
        AbstractGui.blit(x, y, 0, 0, 0, iconCompound.getInt("width"), iconCompound.getInt("height"), 16, 16);
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
        return itemStack.hasTag() && itemStack.getTag().contains(NBT_KEY, 10);
    }
}
