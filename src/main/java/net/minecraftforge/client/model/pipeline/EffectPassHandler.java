package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * The new core for the effect passes, allows for full manipulation.<br>
 * Extend {@link ItemStackEffectPassHandler} for item stack effect handling.<br>
 * Extend {@link ArmorEffectPassHandler} for armor effect handling.
 * */
public abstract class EffectPassHandler<T>
{    
    
    /**
     * Default ItemStack Enchantment Effect
     * */
    public static final ItemStackEffectPassHandler vanillaStackPassHandler = new VanillaItemPassHandler();
    
    /**
     * Default Armor Enchantment Effect
     * */
    public static final ArmorEffectPassHandler vanillaArmorPassHandler = new VanillaArmorPassHandler();

    /**
     * Vanilla's default glint texture
     * */
    public static final ResourceLocation RES_DEFAULT_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    
    /**The number of passes to run through*/
    private final int numberOfPasses;
    
    private EffectPassHandler(int passesToRun)
    {
        numberOfPasses = passesToRun;
    }

    /**
     * Allows for multiple models to be utilized. Note that:<br>
     * <i>1)This will only be used for one pass.<br>
     * 2)Returning null will cause the default model to be used</i>
     * @param theStack the correlated ItemStack
     * @param pass the current pass
     * @return a model to use for a given pass.
     * */
    public T getModelOverrideForPass(ItemStack theStack, int pass)
    {
        return null;
    }
    
    
    /**
     * Returns the required number of passes to run through.
     * */
    public final int getPassTarget()
    {
        return numberOfPasses;
    }

    /**
     * This method interacts with {@link OverlayEffectEvent} to allow the handling of color overrides
     * @param override The int array with a pass -> array identifier correlation
     * */
    public void init(int... override){}
    
    /**
     * Handles the effect passes during ItemStack rendering.
     * */
    public static abstract class ItemStackEffectPassHandler extends EffectPassHandler<IBakedModel>
    {
        /**
         * @param passesToRun The number of passes that are iterated through for this particular handler
         * */
        public ItemStackEffectPassHandler(int passesToRun)
        {
            super(passesToRun);
        }
        
        /**
         * Run before the model is rendered.
         * @param theStack The correlated ItemStack
         * @param textureManager the TextureManager 
         * @param pass the current render pass
         * */
        public abstract void prePass(ItemStack theStack, TextureManager textureManager, int pass);
        
        /**
         * Run after the model is rendered.
         * @param textureManager 
         * @param pass the current render pass
         * */
        public abstract void postPass(ItemStack theStack, TextureManager textureManager, int pass);
        
        /**
         * @return An ARBG integer used for rendering the model
         * */
        public abstract int getColorForPass(ItemStack theStack, int pass);

    }

    /**
     * Handles the effect passes when rendering the armor layers
     * */
    public static abstract class ArmorEffectPassHandler extends EffectPassHandler<ModelBase>
    {
        public ArmorEffectPassHandler(int passesToRun)
        {
            super(passesToRun);
        }
        
        /**
         * Run at the beginning of a pass, before the model is rendered.
         * @param stack The ItemStack
         * @param wearer The Entity that has the armor equipped
         * @param renderer The Renderer instance from the armor, useful for binding textures
         * @param slot The ID of the slot that the armor is in
         * @param pass The effect pass
         * @param partialTicks The partialTick value passed to the armor renderer
         * */
        public abstract void prePass(ItemStack stack, EntityLivingBase wearer, @SuppressWarnings("rawtypes") RendererLivingEntity renderer, int slot, int pass, float partialTicks);

        /**
         * Run at the beginning of a pass, before the model is rendered.
         * @param stack The ItemStack
         * @param wearer The Entity that has the armor equipped
         * @param renderer The Renderer instance from the armor, useful for binding textures
         * @param slot The ID of the slot that the armor is in
         * @param pass The effect pass
         * @param partialTicks The partialTick value passed to the armor renderer
         * */
        public abstract void postPass(ItemStack theStack,EntityLivingBase theWearer, RendererLivingEntity renderer, int slot, int pass, float partialTicks);

        /**
         * @param stack The ItemStack
         * @param wearer The Entity that has the armor equipped
         * @param slot The ID of the slot that the armor is in
         * @param pass The effect pass
         * */
        public abstract int getColorForPass(ItemStack stack, EntityLivingBase wearer, int slot, int pass);

    }

    /*-------------------------Vanilla Encapsulation---------------------------------*/

    /**    
     * Vanilla's ItemStack Enchantment encapsulated in new methodology
     * */
    private static final class VanillaItemPassHandler extends ItemStackEffectPassHandler
    {

        private int[] glintColor;

        public VanillaItemPassHandler()
        {
            super(2); //Vanilla runs 2 passes
        }

        @Override
        public void init(int... override)
        {
            glintColor = override;
        }

        @Override
        public void prePass(ItemStack theStack, TextureManager textureManager, int pass)
        {
            float f = (float)(Minecraft.getSystemTime() % (pass == 0 ? 3000L : 4873L)) / (pass == 0 ? 3000.0F : 4873.0F) / 8.0F;
            if(pass == 0)
            {
                GlStateManager.depthMask(false);
                GlStateManager.depthFunc(514);
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                textureManager.bindTexture(RES_DEFAULT_GLINT);
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scale(8.0F, 8.0F, 8.0F);
                GlStateManager.translate(f, 0.0F, 0.0F);
                GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
            }else
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(8.0F, 8.0F, 8.0F);
                GlStateManager.translate(-f, 0.0F, 0.0F);
                GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
            }
        }

        @Override
        public void postPass(ItemStack theStack, TextureManager theManager, int pass)
        {

            GlStateManager.popMatrix();
            if(pass == 1)
            {
                GlStateManager.matrixMode(5888);
                GlStateManager.blendFunc(770, 771);
                GlStateManager.enableLighting();
                GlStateManager.depthFunc(515);
                GlStateManager.depthMask(true);
            }
        }

        @Override
        public int getColorForPass(ItemStack stack, int pass)
        {
            return glintColor != null && glintColor.length > pass && glintColor[pass] != -1 ? glintColor[pass] : stack.getItem().getItemStackEffectColorForPass(stack, pass);
        }

    }
    /**
     * Vanilla's Armor Enchantment encapsulated in new methodology
     * */
    private static final class VanillaArmorPassHandler extends ArmorEffectPassHandler
    {

        private int[] glintColor;

        public VanillaArmorPassHandler() 
        {
            super(2); //Vanilla runs 2 passes
        }

        @Override
        public void init(int... override)
        {
            glintColor = override;
        }

        @Override
        public void prePass(ItemStack stack,EntityLivingBase wearer, RendererLivingEntity renderer, int slot, int pass, float partialTicks)
        {     
            float f = (float)wearer.ticksExisted + partialTicks;
            if(pass == 0){
                renderer.bindTexture(RES_DEFAULT_GLINT);
                GlStateManager.enableBlend();
                GlStateManager.depthFunc(514);
                GlStateManager.depthMask(false);
            }
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                float f3 = 0.33333334F;
                GlStateManager.scale(f3, f3, f3);
                setColor(getColorForPass(stack, wearer, slot, pass));
                GlStateManager.rotate(30.0F - (float)pass * 60.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translate(0.0F, f * (0.001F + (float)pass * 0.003F) * 20.0F, 0.0F);
                GlStateManager.matrixMode(5888);
        }

        @Override
        public void postPass(ItemStack theStack, EntityLivingBase theWearer,RendererLivingEntity theRenderer, int slot, int pass,float partialTicks)
        {
            if(pass == 1)
            {
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.enableLighting();
                GlStateManager.depthMask(true);
                GlStateManager.depthFunc(515);
                GlStateManager.disableBlend();
            }            
        }

        @Override
        public int getColorForPass(ItemStack stack, EntityLivingBase wearer, int slot, int pass)
        {
            return glintColor != null && glintColor.length > pass && glintColor[pass] != -1 ? glintColor[pass] : stack.getItem().getArmorEffectColorForPass(stack, wearer, slot, pass);
        }

    }

    /**
     * Helper method for feeding the state manager A R G B hex colors.
     * @param glintValue The color to set
     * */
    public static void setColor(int glintValue)
    {
        GlStateManager.color((glintValue >> 16 & 255) / 255.0F, (glintValue >> 8 & 255) / 255.0F,  (glintValue & 255) / 255F , (glintValue >> 24 & 255) / 255.0F);
    }
}