package net.minecraftforge.client;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Interface for controlling ItemStack's effect overlay.
 * These need to be registered using ItemOverlayHandler.registerOverlayHanadler().
 * */
public interface IItemOverlay
{

    /**
     * Vanilla's default/fallback overlay.
     * */
    public static final IItemOverlay VANILLA = new Vanilla();

    /**
     * Vanilla's default Item effect color value.
     * */
    public static final int defaultOverlayColor = 0xFF8040CC;

    /**
     * Vanilla's default Glint texture.
     * */
    public static final ResourceLocation defaultGlint = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    /**
     * Used for effect coloring when the Item's effect passes are run.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * 
     * @return a hex int (ARGB) used for coloring the effect
     * */
    public int getOverlayColor(ItemStack stack, int pass);

    /**
     * Used for timing offsets so it needn't be calculated per vector.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * @param long the time to be shifted
     * 
     * @return a float representing the time frame for the pass
     * */
    public float getShiftedTime(ItemStack stack, int pass, long baseTime);

    /**
     * Used for effect scaling based on time.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * @param time the offset time
     * 
     * @return a 3 value array of floats representing scaling
     * */
    public float[] getOverlayScaleVector(ItemStack stack, int pass, float time);

    /**
     * Used for effect translation based on time.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * @param time the offset time
     * 
     * @return a 3 value array of floats representing translation
     * */
    public float[] getOverlayTranslationVector(ItemStack stack, int pass, float time);

    /**
     * Used for effect rotation based on time.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * @param time the offset time
     * 
     * @return a 4 value array of floats representing rotation
     * */
    public float[] getOverlayRotationVector(ItemStack stack, int pass, float time);

    /**
     * Used for setting the effect texture at the start of a pass.
     * 
     * @param stack the ItemStack in question
     * 
     * @return the ResourceLocation of the effect texture.
     * */
    public ResourceLocation getOverlayTexture(ItemStack stack);

    public static class Vanilla implements IItemOverlay
    {

        private Vanilla(){}

        @Override
        public int getOverlayColor(ItemStack stack, int pass)
        {
            return defaultOverlayColor;
        }

        @Override
        public float[] getOverlayScaleVector(ItemStack stack, int pass, float time)
        {
            return new float[] {8.0F, 8.0F, 8.0F};
        }

        @Override
        public float[] getOverlayTranslationVector(ItemStack stack, int pass, float time)
        {
            return pass == 0 ? new float[] {time, 0.0F, 0.0F} : new float[] {-time, 0.0F, 0.0F};
        }

        @Override
        public float[] getOverlayRotationVector(ItemStack stack, int pass, float time)
        {
            return pass == 0 ? new float[] {-50.0F, 0.0F, 0.0F, 1.0F} : new float[] {10.0F, 0.0F, 0.0F, 1.0F};
        }

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack)
        {
            return defaultGlint;
        }

        @Override
        public float getShiftedTime(ItemStack stack, int pass, long baseTime) {
            return pass == 0 ? (baseTime % 3000L) / 24000.0F : (baseTime % 3873L) / 38984.0F;
        }

    }

}