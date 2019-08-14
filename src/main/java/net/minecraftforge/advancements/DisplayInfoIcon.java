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

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Used for rendering of a texture as an advancement icon instead of need of providing a proxy item.
 */
public class DisplayInfoIcon {
    private static final String KEY_PREFIX = "forgeadvicon:";

    /**
     * Creates a fake itemStack with reference to texture given from advancement json.
     * Existence of "forge" json object is checked in {@link net.minecraft.advancements.DisplayInfo#deserializeIcon()}.
     *
     * @param object advancement json object
     * @return fake itemStack with texture reference
     */
    public static ItemStack createForgeIcon(final JsonObject object) {
        final String path = KEY_PREFIX + object.get("forge").getAsString();

        return new ItemStack(() -> new Item(new Item.Properties()) {
            @Override
            public String getTranslationKey() {
                return path;
            }
        });
    }

    /**
     * Tries to render given itemStack as an advancement icon if item was created by {@link #createForgeIcon()}.
     * 
     * @return true if forge icon item, false if not
     */
    public static boolean renderForgeIcon(final ItemStack itemStack, final int x, final int y) {
        String textureKey = itemStack.getItem().getTranslationKey();

        if(!textureKey.startsWith(KEY_PREFIX))
        {
            return false;
        }

        textureKey = textureKey.substring(KEY_PREFIX.length());

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableAlphaTest();
        GlStateManager.alphaFunc(516, 0.1F);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(textureKey));
        AbstractGui.blit(x, y, 0, 0, 0, 16, 16, 16, 16);
        Minecraft.getInstance().getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
        GlStateManager.disableBlend();
        GlStateManager.disableAlphaTest();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();

        return true;
    }
}