package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;

/**
 * Offshore handler for the Items' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public final class ItemOverlayHandler
{

    /**
     * Number of passes to be run.
     * */
    private static final byte NUM_PASS = 2;

    private static byte CUR_PASS = 0;

    private ItemOverlayHandler(){}

    /**
     * Handles the execution of overlays.
     * @param textureManager the current TextureManager
     * @param stack the stack in question
     * @return true to render the item again, and false to end the pass loop.
     * */
    public static boolean run(TextureManager textureManager, ItemStack stack)
    {
        if(CUR_PASS == 0)
            begin(textureManager, stack);
        else if(CUR_PASS < NUM_PASS) //Run pass
            {
                GlStateManager.popMatrix();
                prePass(stack);
            }
        else //Cleanup
        {
            end(textureManager);
            CUR_PASS = 0;
            return false;
        }
        CUR_PASS++;
        return true;
    }

    /**
     * Run BEFORE the first pass, setting up the GL state
     * */
    private static void begin(TextureManager textureManager, ItemStack stack)
    {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        GlStateManager.matrixMode(5890);
        textureManager.bindTexture(stack.getItem().getItemEffectTexture(stack));
        prePass(stack);
    }

    /**
     * Run BEFORE each pass
     * @param stack 
     **/
    private static void prePass(ItemStack stack)
    {
        GlStateManager.pushMatrix();
        float time = (float)(Minecraft.getSystemTime() % stack.getItem().getItemEffectTimeRepeatForPass(stack, CUR_PASS)) / stack.getItem().getItemEffectSpeed(stack, CUR_PASS);
        float[] vec = stack.getItem().getItemEffectScaleVectorForPass(stack, CUR_PASS, time);
        GlStateManager.scale(vec[0], vec[1], vec[2]);
        vec = stack.getItem().getItemEffectTranslationVectorForPass(stack, CUR_PASS, time);
        GlStateManager.translate(vec[0], vec[1], vec[2]);
        vec = stack.getItem().getItemEffectRotationVectorForPass(stack, CUR_PASS, time);
        GlStateManager.rotate(vec[0], vec[1], vec[2], vec[3]);
    }

    /**
     * Run AFTER the last pass, reverting the GL State
     * */
    private static void end(TextureManager textureManager)
    {
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        textureManager.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

    /**
     * Helper method for accessing pass color externally
     * @return The effect color for the current pass.
     * */
    public static int getColor(ItemStack stack)
    {
        return stack.getItem().getItemEffectColorForPass(stack, CUR_PASS);
    }
}
