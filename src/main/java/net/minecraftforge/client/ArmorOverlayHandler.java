package net.minecraftforge.client;

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

/**
 * Offshore handler for the Armors' extra passes. Specifically for reducing patch size & maintaining readability.
 * */
public class ArmorOverlayHandler
{

    /**
     * Map of active Item.delegate > IAmorOverlay entries.
     * */
    private static final Map<RegistryDelegate<Item>, IArmorOverlay> armorOverlayMap = Maps.newHashMap();

    /**
     * Number of passes to be run.
     * */
    private static final byte NUM_PASS = 2;

    private ArmorOverlayHandler(){}

    /**
     * Handles the execution of overlays. This feeds from the {@value armorOverlayMap} for custom IIArmorOverlays to be run in lieu of the encapsulated vanilla
     *  methodology. 
     * 
     * @return returns false canceling vanilla's default overlay for minimal patch size
     * */
    public static boolean applyForgeOverlay(TextureManager textureManager, ItemStack stack, EntityLivingBase wearer, float partialTicks, EntityEquipmentSlot slot, ModelBase model, float swing, float swingScale, float headYaw, float headPitch, float scale)
    {
        IArmorOverlay current = getArmorOverlayForItem(stack.getItem());
        float[] vec;
        int color;
        float time = wearer.ticksExisted + partialTicks;
        textureManager.bindTexture(current.getOverlayTexture(stack, wearer));
        GlStateManager.enableBlend();
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        for(int CUR_PASS = 0; CUR_PASS < NUM_PASS; CUR_PASS++)
        {
            color = current.getOverlayColor(stack, wearer, CUR_PASS, slot);
            if(((color >> 16 & 255) | (color >> 8 & 255) | (color & 255)) >= 0xA1)
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            else
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE_MINUS_SRC_COLOR, GlStateManager.DestFactor.ONE_MINUS_DST_COLOR);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.color((color >> 16 & 255) / 255.0F, (color >> 8 & 255) / 255.0F,  (color & 255) / 255F , (color >> 24 & 255) / 255.0F);

            vec = current.getOverlayScaleVector(stack, wearer, CUR_PASS, slot, time);
            GlStateManager.scale(vec[0], vec[1], vec[2]);

            vec = current.getOverlayRotationVector(stack, wearer, CUR_PASS, slot, time);
            GlStateManager.rotate(vec[0], vec[1], vec[2], vec[3]);

            vec = current.getOverlayTranslationVector(stack, wearer, CUR_PASS, slot, time);
            GlStateManager.translate(vec[0], vec[1], vec[2]);

            GlStateManager.matrixMode(5888);
            model.render(wearer, swing, swingScale, time, headYaw, headPitch, scale);
        }
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
     * Registers an instance of IArmorOverlay to a group of items
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

    /**
     * Returns the corresponding IArmorOverlay for the given Item. Falls back to IArmorOverlay.VANILLA if no entry exists. 
     * 
     * @param item the Item in question
     * */
    public static IArmorOverlay getArmorOverlayForItem(Item item)
    {
        final IArmorOverlay entry = armorOverlayMap.get(item.delegate); //One lookup
        return entry == null ? IArmorOverlay.VANILLA : entry;
    }

}
