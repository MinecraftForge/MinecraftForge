package net.minecraftforge.client;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public interface IArmorOverlay
{

    /**
     * Vanilla's default armor overlay color value. Uses half the alpha value than the Item color.
     * */
    public static final int defaultOverlayColor = 0xAA8040CC;

    /**
     * Vanilla's default Glint texture.
     * */
    public static final ResourceLocation defaultGlint = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    public static final IArmorOverlay VANILLA = new Vanilla();

    /**
     * Used for overlay coloring when the Armor's effect passes are run.
     * 
     * @param stack the ItemStack
     * @param wearer the Entity in question
     * @param pass the render pass
     * @param armorSlot the slot the armor is in
     * 
     * @return a hex int (ARGB) used for coloring the effect
     * */
    public int getOverlayColor(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot);

    /**
     * Used for effect scaling based on time.
     * 
     * @param stack the ItemStack
     * @param wearer the Entity in question
     * @param pass the render pass
     * @param slot the slot the armor is in
     * @param time the offset time
     * 
     * @return a 3 value array of floats representing scaling
     * */
    public float[] getOverlayScaleVector(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time);

    /**
     * Used for effect translation based on time.
     * 
     * @param stack the ItemStack
     * @param wearer the Entity in question
     * @param pass the render pass
     * @param slot the slot the armor is in
     * @param time the offset time
     * 
     * @return a 3 array of floats representing translation
     * */
    public float[] getOverlayTranslationVector(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time);
    /**
     * Used for effect rotation based on time.
     * 
     * @param stack the ItemStack
     * @param wearer the Entity in question
     * @param pass the render pass
     * @param slot the slot the armor is in
     * @param time the offset time
     * 
     * @return a 4 value array of floats representing rotation
     * */
    public float[] getOverlayRotationVector(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time);

    /**
     * Used for setting the effect texture at the start of a pass.
     * 
     * @param stack the ItemStack in question
     * @param wearer the Entity in question
     * 
     * @return the ResourceLocation of the effect texture.
     * */
    public ResourceLocation getOverlayTexture(ItemStack stack, EntityLivingBase wearer);
    
    public static class Vanilla implements IArmorOverlay
    {

        private Vanilla(){}

        @Override
        public int getOverlayColor(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot)
        {
            return defaultOverlayColor;
        }

        @Override
        public float[] getOverlayScaleVector(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time)
        {
            return new float[] {0.33333334F, 0.33333334F, 0.33333334F};
        }

        @Override
        public float[] getOverlayTranslationVector(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time)
        {
            return new float[] {0.0F, time * (pass == 0 ? 0.0006F : 0.06006F), 0.0F};
        }

        @Override
        public float[] getOverlayRotationVector(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time)
        {
            return new float[] {(pass == 0 ? 30F : -30F), 0.0F, 0.0F, 1.0F};
        }

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack, EntityLivingBase wearer)
        {
            return defaultGlint;
        }

    }

}
