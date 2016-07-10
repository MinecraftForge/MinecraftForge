package net.minecraftforge.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Offshore handler for the Items' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public final class ItemOverlayHandler
{

    private static final java.util.Map<net.minecraftforge.fml.common.registry.RegistryDelegate<Item>, IItemOverlay> itemOverlayMap = com.google.common.collect.Maps.newHashMap();

    private static IItemOverlay current;

    private static final byte NUM_PASS = 2;

    private static byte CUR_PASS = 0;

    private ItemOverlayHandler(){}

    /**
     * Handles the execution of overlays.
     * 
     * @param textureManager the current TextureManager
     * @param stack the stack in question
     * 
     * @return true to render the item again, and false to end the pass loop.
     * */
    public static boolean run(TextureManager textureManager, ItemStack stack)
    {
        if(CUR_PASS == 0)
        {
            current = itemOverlayMap.get(stack.getItem().delegate);
            if(current == null)
               current = IItemOverlay.VANILLA;
            begin(textureManager, stack);
        }
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
        textureManager.bindTexture(current.getOverlayTexture(stack));
        prePass(stack);
    }

    /**
     * Run BEFORE each pass
     **/
    private static void prePass(ItemStack stack)
    {
        GlStateManager.pushMatrix();
        float time = (float)(Minecraft.getSystemTime() % current.getOverlayRepeat(stack, CUR_PASS)) / current.getOverlaySpeed(stack, CUR_PASS);
        float[] vec = current.getOverlayScaleVector(stack, CUR_PASS, time);
        GlStateManager.scale(vec[0], vec[1], vec[2]);
        vec = current.getOverlayTranslationVector(stack, CUR_PASS, time);
        GlStateManager.translate(vec[0], vec[1], vec[2]);
        vec = current.getOverlayRotationVector(stack, CUR_PASS, time);
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
     * 
     * @return the effect color for the current pass.
     * */
    public static int getColor(ItemStack stack)
    {
        return current.getOverlayColor(stack, CUR_PASS);
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
