package net.minecraftforge.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * Offshore handler for the Armors' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public class ArmorOverlayHandler
{
    /**
     * Number of passes to be run.
     * */
    private static final byte NUM_PASS = 2;

    private static byte CUR_PASS = 0;

    private ArmorOverlayHandler(){}

    /**
     * Handles the execution of overlays.
     * */
    public static boolean run(TextureManager textureManager, ItemStack stack, EntityLivingBase wearer, float partialTicks, EntityEquipmentSlot slot)
    {
        if(CUR_PASS == 0)
            begin(textureManager, stack, wearer, partialTicks, slot);
        else if(CUR_PASS < NUM_PASS) //Run pass
            {
                prePass(stack, wearer, partialTicks, slot);
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
    private static void begin(TextureManager textureManager, ItemStack stack, EntityLivingBase wearer, float partialTicks, EntityEquipmentSlot slot)
    {
        textureManager.bindTexture(stack.getItem().getArmorEffectTexture(stack, wearer));
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
        prePass(stack, wearer, partialTicks, slot);
    }

    /**
     * Run BEFORE each pass
     * @param stack 
     * @param partialTicks 
     * @param wearer 
     * @param slot 
     **/
    private static void prePass(ItemStack stack, EntityLivingBase wearer, float partialTicks, EntityEquipmentSlot slot)
    {
        int color = stack.getItem().getArmorEffectColorForPass(stack, wearer, CUR_PASS, slot);
        float time = wearer.ticksExisted + partialTicks;
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F,  (color & 255) / 255F , (color >> 24 & 255) / 255.0F);
        float[] vec = stack.getItem().getArmorEffectScaleVectorForPass(stack, wearer, CUR_PASS, slot, time);
        GlStateManager.scale(vec[0], vec[1], vec[2]);
        vec = stack.getItem().getArmorEffectRotationVectorForPass(stack, wearer, CUR_PASS, slot, time);
        GlStateManager.rotate(vec[0], vec[1], vec[2], vec[3]);
        vec = stack.getItem().getArmorEffectTranslationVectorForPass(stack, wearer, CUR_PASS, slot, time);
        GlStateManager.translate(vec[0], vec[1], vec[2]);
        GlStateManager.matrixMode(5888);
    }

    /**
     * Run AFTER the last pass, reverting the GL State
     * */
    private static void end(TextureManager textureManager)
    {
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
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
