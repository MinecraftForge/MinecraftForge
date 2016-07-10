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
     * Used for effect repeat. Vanilla repeats every 3k ms for pass 0 & 4873ms for pass 1.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * 
     * @return a long representing repeat time in milliseconds
     * */
    public long getOverlayRepeat(ItemStack stack, int pass);

    /**
     * Used for effect TimeScaling
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * 
     * @return A float representing scaling time in milliseconds
     * */
    public float getOverlaySpeed(ItemStack stack, int pass);

    /**
     * Used for effect scaling based on time.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * @param time the offset time
     * 
     * @return A 3 value array of floats representing scaling
     * */
    public float[] getOverlayScaleVector(ItemStack stack, int pass, float time);

    /**
     * Used for effect translation based on time.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * @param time the offset time
     * 
     * @return A 3 value array of floats representing translation
     * */
    public float[] getOverlayTranslationVector(ItemStack stack, int pass, float time);

    /**
     * Used for effect rotation based on time.
     * 
     * @param stack the ItemStack
     * @param pass the render pass
     * @param time the offset time
     * 
     * @return A 4 value array of floats representing rotation
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

        public int getOverlayColor(ItemStack stack, int pass)
        {
            return defaultOverlayColor;
        }

        public long getOverlayRepeat(ItemStack stack, int pass)
        {
            return pass == 0 ? 3000L : 4873L;
        }

        public float getOverlaySpeed(ItemStack stack, int pass)
        {
            return pass == 0 ? 24000.0F : 38984.0F;
        }

        public float[] getOverlayScaleVector(ItemStack stack, int pass, float time)
        {
            return new float[] {8.0F, 8.0F, 8.0F};
        }

        public float[] getOverlayTranslationVector(ItemStack stack, int pass, float time)
        {
            return pass == 0 ? new float[] {time, 0.0F, 0.0F} : new float[] {-time, 0.0F, 0.0F};
        }

        public float[] getOverlayRotationVector(ItemStack stack, int pass, float time)
        {
            return pass == 0 ? new float[] {-50.0F, 0.0F, 0.0F, 1.0F} : new float[] {10.0F, 0.0F, 0.0F, 1.0F};
        }

        public ResourceLocation getOverlayTexture(ItemStack stack)
        {
            return defaultGlint;
        }

    }

}