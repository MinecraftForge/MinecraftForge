package net.minecraftforge.client;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Offshore handler for the Armors' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public class ArmorOverlayHandler
{

    private static final java.util.Map<net.minecraftforge.fml.common.registry.RegistryDelegate<Item>, IArmorOverlay> armorOverlayMap = com.google.common.collect.Maps.newHashMap();

    private static IArmorOverlay current;

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
        {
            current = armorOverlayMap.get(stack.getItem().delegate);
            if(current == null)
                current = IArmorOverlay.VANILLA;
            begin(textureManager, stack, wearer, partialTicks, slot);
        }
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
        textureManager.bindTexture(current.getOverlayTexture(stack, wearer));
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
        int color = current.getOverlayColor(stack, wearer, CUR_PASS, slot);
        float time = wearer.ticksExisted + partialTicks;
        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F,  (color & 255) / 255F , (color >> 24 & 255) / 255.0F);
        float[] vec = current.getOverlayScaleVector(stack, wearer, CUR_PASS, slot, time);
        GlStateManager.scale(vec[0], vec[1], vec[2]);
        vec = current.getOverlayRotationVector(stack, wearer, CUR_PASS, slot, time);
        GlStateManager.rotate(vec[0], vec[1], vec[2], vec[3]);
        vec = current.getOverlayTranslationVector(stack, wearer, CUR_PASS, slot, time);
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
     * Registers an instance of IItemOverlay to a group of items
     * 
     * @param armorOverlay the overlay being registered
     * @param itemsIn the items corresponding to the armor that will be using the overlay
     * */
    public static void registerOverlayHandler(IArmorOverlay armorOverlay, Item... itemsIn)
    {
        for (Item item : itemsIn)
        {
            if (item == null) throw new IllegalArgumentException("Item registered to item overlay handler cannot be null!");
            if (item.getRegistryName() == null) throw new IllegalArgumentException("Item must be registered before assigning an overlay handler.");
            armorOverlayMap.put(item.delegate, armorOverlay);
        }
    }

}
