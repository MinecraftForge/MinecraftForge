package net.minecraftforge.advancements;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DisplayInfoIcon {
    private static final String KEY_PREFIX = "forgeadvicon:";

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
     * @return true if forge item, false if not
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