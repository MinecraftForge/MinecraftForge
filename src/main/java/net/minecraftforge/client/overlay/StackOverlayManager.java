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

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.registry.RegistryDelegate;

/**
 * Offshore handler for the Items' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public final class StackOverlayManager
{

    /**
     * Map of active Item.delegate > IStackOverlayHandler entries.
     * */
    private static final Map<RegistryDelegate<Item>, IStackOverlayHandler> handlerMap = Maps.newHashMap();

    /**
     * Map of active Item.delegate > IStackOverlayColor entries.
     * */
    private static final Map<RegistryDelegate<Item>, IStackOverlayColor> colorMap = Maps.newHashMap();

    /**
     * Map of the active Item.delegate > StackOverlayColor entries.
     * */

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

    private StackOverlayManager(){}

    /**
     * Handles the execution of overlays.
     *
     * @return returns false canceling vanilla's default overlay for minimal patch size
     * */
    public static boolean applyForgeOverlay(TextureManager textureManager, ItemStack stack, IBakedModel model)
    {
        long time = Minecraft.getSystemTime();
        IStackOverlayHandler currentHandler = stack.getItem().getStackOverlayHandler(stack);
        IStackOverlayColor currentColor = stack.getItem().getStackOverlayColor(stack);

        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.matrixMode(5890);
        textureManager.bindTexture(currentHandler.getOverlayTexture(stack));

        //----------------FIRST PASS

        int color = currentColor.getFirstPassColor(stack);

        if(((color >> 16 & 255) | (color >> 8 & 255) | (color & 255)) >= 0xA1)
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        else
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_DST_COLOR);
        GlStateManager.pushMatrix();

        currentHandler.manageFirstPassVectors(stack, time, scaleVector, rotationVector, translationVector);

        GlStateManager.scale(scaleVector[0], scaleVector[1], scaleVector[2]);
        GlStateManager.rotate(rotationVector[0], rotationVector[1], rotationVector[2], rotationVector[3]);
        GlStateManager.translate(translationVector[0], translationVector[1], translationVector[2]);

        renderModelStreamlined(model, color);
        GlStateManager.popMatrix();

        //----------------SECOND PASS

        color = currentColor.getSecondPassColor(stack);

        if(((color >> 16 & 255) | (color >> 8 & 255) | (color & 255)) >= 0xA1)
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        else
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_DST_COLOR);
        GlStateManager.pushMatrix();

        currentHandler.manageSecondPassVectors(stack, time, scaleVector, rotationVector, translationVector);

        GlStateManager.scale(scaleVector[0], scaleVector[1], scaleVector[2]);
        GlStateManager.rotate(rotationVector[0], rotationVector[1], rotationVector[2], rotationVector[3]);
        GlStateManager.translate(translationVector[0], translationVector[1], translationVector[2]);

        renderModelStreamlined(model, color);
        GlStateManager.popMatrix();

        //----------------FIN

        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        return false;
    }

    private static void renderModelStreamlined(IBakedModel model, int color)
    {
        Tessellator tessellator = Tessellator.getInstance();
        VertexBuffer vertexbuffer = tessellator.getBuffer();
        vertexbuffer.begin(7, DefaultVertexFormats.ITEM);
        for (EnumFacing enumfacing : EnumFacing.values())
            renderStreamlinedQuads(vertexbuffer, model.getQuads((IBlockState)null, enumfacing, 0L), color);
        renderStreamlinedQuads(vertexbuffer, model.getQuads((IBlockState)null, (EnumFacing)null, 0L), color);
        tessellator.draw();
    }

    private static void renderStreamlinedQuads(VertexBuffer renderer, List<BakedQuad> quads, int color)
    {
        for (BakedQuad quad : quads)
            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, quad, color);
    }

    /**
     * Registers an instance of IItemOverlay to a group of items
     *
     * @param itemOverlay the overlay being registered
     * @param itemsIn the items that will be using the overlay
     * */
    @Deprecated
    public static void registerHandler(IStackOverlayHandler itemOverlay, Item... itemsIn)
    {
        for (Item item : itemsIn)
        {
            if (item == null) throw new IllegalArgumentException("Item registered to item overlay handler cannot be null!");
            if (item.getRegistryName() == null) throw new IllegalArgumentException("Item must be registered before assigning an overlay handler.");
            handlerMap.put(item.delegate, itemOverlay);
        }
    }

    /**
     * Returns the corresponding IItemOverlay for the given Item. Falls back to IItemOverlay.VANILLA if no entry exists.
     *
     * @param item the Item in question
     * */
    public static IStackOverlayHandler getHandler(Item item)
    {
        final IStackOverlayHandler entry = handlerMap.get(item.delegate);
        return entry == null ? IStackOverlayHandler.VANILLA : entry;
    }

    /**
     * Registers an instance of IStackolor to a group of items
     *
     * @param color the color being registered
     * @param itemsIn the items that will be using the overlay
     * */
    @Deprecated
    public static void registerColor(IStackOverlayColor color, Item... itemsIn)
    {
        for (Item item : itemsIn)
        {
            if (item == null) throw new IllegalArgumentException("Item registered to item overlay handler cannot be null!");
            if (item.getRegistryName() == null) throw new IllegalArgumentException("Item must be registered before assigning an overlay handler.");
            colorMap.put(item.delegate, color);
        }
    }

    /**
     * Returns the corresponding IStackOverlayColor for the given Item. Falls back to IStackOverlay.VANILLA if no entry exists.
     *
     * @param item the Item in question
     * */
    public static IStackOverlayColor getColor(Item item)
    {
        final IStackOverlayColor entry = colorMap.get(item.delegate);
        return entry == null ? IStackOverlayColor.VANILLA : entry;
    }
}