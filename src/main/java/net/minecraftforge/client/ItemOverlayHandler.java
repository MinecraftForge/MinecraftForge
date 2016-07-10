package net.minecraftforge.client;

import java.util.List;

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
import net.minecraftforge.common.ForgeModContainer;

/**
 * Offshore handler for the Items' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public final class ItemOverlayHandler
{

    private static final java.util.Map<net.minecraftforge.fml.common.registry.RegistryDelegate<Item>, IItemOverlay> itemOverlayMap = com.google.common.collect.Maps.newHashMap();

    private static final byte NUM_PASS = 2;

    private ItemOverlayHandler(){}

    /**
     * Handles the execution of overlays.
     * 
     * @param textureManager the current TextureManager
     * @param stack the stack in question
     * @param model 
     * 
     * @return true to render the item again, and false to end the pass loop.
     * */
    public static boolean run(TextureManager textureManager, ItemStack stack, IBakedModel model)
    {
        if(ForgeModContainer.forgeItemOverlay)
        {
            float[] vec;
            int color;
            float time;
            IItemOverlay current = itemOverlayMap.get(stack.getItem().delegate);
            if(current == null)
               current = IItemOverlay.VANILLA;
            GlStateManager.depthMask(false);
            GlStateManager.depthFunc(514);
            GlStateManager.disableLighting();
            GlStateManager.matrixMode(5890);
            textureManager.bindTexture(current.getOverlayTexture(stack));
            
            for(int CUR_PASS = 0; CUR_PASS < NUM_PASS; CUR_PASS++)
            {
                color = current.getOverlayColor(stack, CUR_PASS);
                if(((color >> 16 & 255) | (color >> 8 & 255) | (color & 255)) >= 0xA1)
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                else
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_DST_COLOR);
                time = (float)(Minecraft.getSystemTime() % current.getOverlayRepeat(stack, CUR_PASS)) / current.getOverlaySpeed(stack, CUR_PASS);
                GlStateManager.pushMatrix();
 
                vec = current.getOverlayScaleVector(stack, CUR_PASS, time);
                GlStateManager.scale(vec[0], vec[1], vec[2]);

                vec = current.getOverlayTranslationVector(stack, CUR_PASS, time);
                GlStateManager.translate(vec[0], vec[1], vec[2]);

                vec = current.getOverlayRotationVector(stack, CUR_PASS, time);
                GlStateManager.rotate(vec[0], vec[1], vec[2], vec[3]);

                renderModelStreamlined(model, color);
                GlStateManager.popMatrix();
            }
            GlStateManager.matrixMode(5888);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableLighting();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
            return false;
        }
        return true;
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
    public static void registerOverlayHandler(IItemOverlay itemOverlay, Item... itemsIn)
    {
        for (Item item : itemsIn)
        {
            if (item == null) throw new IllegalArgumentException("Item registered to item overlay handler cannot be null!");
            if (item.getRegistryName() == null) throw new IllegalArgumentException("Item must be registered before assigning an overlay handler.");
            itemOverlayMap.put(item.delegate, itemOverlay);
        }
    }

}
