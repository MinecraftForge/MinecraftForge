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

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

import javax.annotation.Nonnull;

/**
 * Offshore handler for the Armors' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public class ArmorOverlayManager
{

    /**
     * Map of active Item.delegate > IArmorOverlayHandler entries.
     * */
    private static final Map<RegistryDelegate<Item>, IArmorOverlayHandler> handlerMap = Maps.newHashMap();

    /**
     * Map of active Item.delegate > IArmorOverlayColor entries.
     * */
    private static final Map<RegistryDelegate<Item>, IArmorOverlayColor> colorMap = Maps.newHashMap();

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
        IArmorOverlayHandler currentHandler = stack.getItem().getArmorOverlayHandler(stack);
        IArmorOverlayColor currentColor = stack.getItem().getArmorOverlayColor(stack);
        final float time = wearer.ticksExisted + partialTicks;
        textureManager.bindTexture(currentHandler.getOverlayTexture(stack, wearer));
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

    /**
     *  Do not use for items packaged with your mod. Instead directly override Item.getArmorOverlayHandler<br><br>
     *
     *  Registers an instance of IArmorOverlayHandler to a group of items
     *
     * @param handler the handler being registered
     * @param itemsIn the items corresponding to the armor that will be using the overlay
     * */
    @Deprecated
    public static void registerHandler(@Nonnull IArmorOverlayHandler handler, @Nonnull Item... itemsIn)
    {
        for (Item item : itemsIn)
        {
            if (item == null) throw new IllegalArgumentException("Item registered to armor overlay handler cannot be null!");
            if (item.getRegistryName() == null) throw new IllegalArgumentException("Item must be registered before assigning an overlay handler.");
            handlerMap.put(item.delegate, handler);
        }
    }

    /**
     * Do not use for items packaged with your mod. Instead directly override Item.getArmorOverlayColor<br><br>
     *
     * Registers an instance of IArmorOverlayHandler to a group of items
     *
     * @param color the handler being registered
     * @param itemsIn the items corresponding to the armor that will be using the overlay
     * */
    @Deprecated
    public static void registerColor(@Nonnull IArmorOverlayColor color, @Nonnull Item... itemsIn)
    {
        for (Item item : itemsIn)
        {
            if (item == null) throw new IllegalArgumentException("Item registered to armor overlay handler cannot be null!");
            if (item.getRegistryName() == null) throw new IllegalArgumentException("Item must be registered before assigning an overlay handler.");
            colorMap.put(item.delegate, color);
        }
    }

    /**
     * Fallback map lookup for handlers
     * */
    public static IArmorOverlayHandler getHandler(Item item)
    {
        IArmorOverlayHandler entry = handlerMap.get(item.delegate);
        return  entry == null ? IArmorOverlayHandler.VANILLA : entry;
    }

    /**
     * Fallback map lookup for colors
     * */
    public static IArmorOverlayColor getColor(Item item)
    {
        IArmorOverlayColor entry = colorMap.get(item.delegate);
        return entry == null ? IArmorOverlayColor.VANILLA : entry;
    }
}