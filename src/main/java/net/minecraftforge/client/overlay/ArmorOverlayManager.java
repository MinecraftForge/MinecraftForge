/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.client.overlay;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Offshore handler for the Armors' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public class ArmorOverlayManager
{

    private ArmorOverlayManager(){}

    /**
     * Static Vec3 passed for scaling
     * */
    private static final float[] scaleVector = new float[3];

    /**
     * Static Vec4 passed for rotation
     * */
    private static final float[] rotationVector = new float[4];

    /**
     * Static Vec3 passsed for translation
     * */
    private static final float[] translationVector = new float[3];

    /**
     * Handles the execution of overlays.
     *
     * @return returns false canceling vanilla's default overlay for minimal patch size
     * */
    public static boolean applyForgeOverlay(TextureManager textureManager, ItemStack stack, EntityLivingBase wearer, float partialTicks, EntityEquipmentSlot slot, ModelBase model, float swing, float swingScale, float headYaw, float headPitch, float scale)
    {
        IArmorOverlayHandler currentHandler = ((ItemArmor)stack.getItem()).getArmorOverlayHandler();
        IArmorOverlayColor currentColor = ((ItemArmor)stack.getItem()).getArmorOverlayColor();
        final float time = wearer.ticksExisted + partialTicks;
        textureManager.bindTexture(currentHandler.getOverlayTexture(stack, wearer, slot));
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        GlStateManager.disableLighting();

        //----------------FIRST PASS

        int color = currentColor.getFirstPassColor(stack, wearer, slot);
        if(((color >> 16 & 255) | (color >> 8 & 255) | (color & 255)) >= 0xA1)
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        else
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_DST_COLOR);

        GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F,  (color & 255) / 255F , (color >> 24 & 255) / 255.0F);

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();

        currentHandler.manageFirstPassVectors(stack, wearer, slot, time, scaleVector, rotationVector, translationVector);

        GlStateManager.scale(scaleVector[0], scaleVector[1], scaleVector[2]);
        GlStateManager.rotate(rotationVector[0], rotationVector[1], rotationVector[2], rotationVector[3]);
        GlStateManager.translate(translationVector[0], translationVector[1], translationVector[2]);

        GlStateManager.matrixMode(5888);
        model.render(wearer, swing, swingScale, time, headYaw, headPitch, scale);

        //----------------Second Pass

        color = currentColor.getSecondPassColor(stack, wearer, slot);
        if(((color >> 16 & 255) | (color >> 8 & 255) | (color & 255)) >= 0xA1)
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        else
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_DST_COLOR);

        GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F,  (color & 255) / 255F , (color >> 24 & 255) / 255.0F);

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();

        currentHandler.manageSecondPassVectors(stack, wearer, slot, time, scaleVector, rotationVector, translationVector);

        GlStateManager.scale(scaleVector[0], scaleVector[1], scaleVector[2]);
        GlStateManager.rotate(rotationVector[0], rotationVector[1], rotationVector[2], rotationVector[3]);
        GlStateManager.translate(translationVector[0], translationVector[1], translationVector[2]);

        GlStateManager.matrixMode(5888);
        model.render(wearer, swing, swingScale, time, headYaw, headPitch, scale);

        //----------------Fin

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        return false;
    }

}