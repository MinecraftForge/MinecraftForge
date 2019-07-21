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
 *//*


package net.minecraftforge.debug.gameplay;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.Random;

//@Mod.EventBusSubscriber
//@Mod(modid = PotionRegistryTest.MODID, name = "ForgePotionRegistry", version = "1.0", acceptableRemoteVersions = "*")
public class PotionRegistryTest
{
    public static final String MOD_ID = "forge_potion_registry";

    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event)
    {
        event.getRegistry().register(
                new PotionForge(false, 0xff00ff)
                        .setRegistryName(MOD_ID, "forge")
                        .setPotionName("potion." + MOD_ID + ".forge")
        );
    }

    private static class PotionForge extends Potion
    {
        PotionForge(boolean badEffect, int potionColor)
        {
            super(badEffect, potionColor);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z)
        {
            Potion potion = effect.getPotion();

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_0");

            x += 6;
            y += 7;

            int width = 18;
            int height = width;

            float r = (float) (potion.getLiquidColor() >> 24 & 255) / 255.0F;
            float g = (float) (potion.getLiquidColor() >> 16 & 255) / 255.0F;
            float b = (float) (potion.getLiquidColor() >> 8 & 255) / 255.0F;
            float a = (float) (potion.getLiquidColor() & 255) / 255.0F;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buf = tessellator.getBuffer();
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            GlStateManager.color(r, g, b, a);

            buf.pos((double) x, (double) (y + height), z).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
            buf.pos((double) (x + width), (double) (y + height), z).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
            buf.pos((double) (x + width), (double) y, z).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
            buf.pos((double) x, (double) y, z).tex(sprite.getMinU(), sprite.getMinV()).endVertex();

            tessellator.draw();
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha)
        {
            Potion potion = effect.getPotion();

            Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            TextureAtlasSprite sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite("minecraft:blocks/tnt_side");

            x += 3;
            y += 3;

            int width = 18;
            int height = width;

            float r = (float) (potion.getLiquidColor() >> 24 & 255) / 255.0F;
            float g = (float) (potion.getLiquidColor() >> 16 & 255) / 255.0F;
            float b = (float) (potion.getLiquidColor() >> 8 & 255) / 255.0F;

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buf = tessellator.getBuffer();
            buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            GlStateManager.color(r, g, b, alpha);
            buf.pos((double) x, (double) (y + height), z).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
            buf.pos((double) (x + width), (double) (y + height), z).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
            buf.pos((double) (x + width), (double) y, z).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
            buf.pos((double) x, (double) y, z).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
            tessellator.draw();
        }
    }
}
*/
