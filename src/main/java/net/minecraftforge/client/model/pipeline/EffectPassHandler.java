package net.minecraftforge.client.model.pipeline;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * The new core for the effect passes, allows for full GLState manipulation.<br>
 * These are made to act as singletons. Treat them as such, and store them as constants.<br><br>
 * Return in the respective getEffectHandlerFor hooks in Item classes<br>
 * Extend {@link ItemStackEffectPassHandler} for item stack effect handling.<br>
 * Extend {@link ArmorEffectPassHandler} for armor effect handling.
 * */
public abstract class EffectPassHandler<TT>
{

    /**
     * Vanilla/Default ItemStack Enchantment Effect
     * */
    public static final ItemStackEffectPassHandler vanillaStackPassHandler = new VanillaItemPassHandler();

    /**
     * Vanilla/Default Armor Enchantment Effect
     * */
    public static final ArmorEffectPassHandler vanillaArmorPassHandler = new VanillaArmorPassHandler();

    /**
     * Vanilla's glint texture
     * */
    public static final ResourceLocation RES_DEFAULT_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");

    
    /**
     * Whether or not isEssential should return true or false, respectively
     * */
    private final boolean essential;

    private EffectPassHandler(boolean isEssential)
    {
        essential = isEssential;
    }
    
    /**
     * @return whether or not this EffectPassHandler is essential(true) or simply vanity(false)
     * */
    public final boolean isEssential()
    {
        return essential;
    }

    /**
     * Method that passes down the respective method for binding a texture.<br>
     * Do what you can to not utilize multiple textures in a given EffectPassHandler.
     * @param textureAccess the respective access for binding a texture<br>
     * {@link TextureManager#bindTexture(ResourceLocation)} for {@link ItemStackEffectPassHandler}
     * {@link RendererLivingEntity#bindTexture(ResourceLocation)} for {@link ArmorEffectPassHandler}
     * */
    public abstract void bindTexture(TT textureAccess);

    /**
     * Handles the effect passes during ItemStack rendering.
     * */
    public static abstract class ItemStackEffectPassHandler extends EffectPassHandler<TextureManager>
    {
        
        /**
         * @param isEssential whether or not this effect is essential for conveying information
         * */
        protected ItemStackEffectPassHandler(boolean isEssential)
        {
            super(isEssential);
        }
        
        /**
         * Run before the baked model is rendered on a given pass.
         * @param stack The correlated ItemStack
         * @param textureManager the TextureManager 
         * @param pass the current render pass
         * */
        public abstract void prePass(ItemStack stack, int pass);
        
        /**
         * Run after the baked model is rendered on a given pass.
         * @param textureManager 
         * @param pass the current render pass
         * */
        public abstract void postPass(ItemStack stack, int pass);
        
        /**
         * @return An ARBG integer used for rendering the model
         * */
        public abstract int getColorForPass(ItemStack stack, int pass);

        /**
         * Returns the required number of passes to run through. Defaults to two for vanilla.<br>
         * <i> Note: the ID of the pass is 1 less than the actual pass.</i><br>
         * Useful for initialization.
         * @param stack the ItemStack being rendered
         * @return the number of passes to run through.<br>
         * */
        public abstract int getPassTarget(ItemStack stack);

    }

    /**
     * Handles the effect passes when rendering the armor models.
     * */
    public static abstract class ArmorEffectPassHandler extends EffectPassHandler<RendererLivingEntity<? extends EntityLivingBase>>
    {
        
        /**
         * @param isEssential whether or not this effect is essential for conveying information
         * */
        protected ArmorEffectPassHandler(boolean isEssential)
        {
            super(isEssential);
        }
        
        /**
         * Run at the beginning of a pass, before the model is rendered.
         * @param stack The ItemStack
         * @param wearer The Entity that has the armor equipped
         * @param slot The ID of the slot that the armor is in
         * @param pass The effect pass
         * @param partialTicks The partialTick value passed to the armor renderer
         * */
        public abstract void prePass(ItemStack stack, EntityLivingBase wearer, int slot, int pass, float partialTicks);

        /**
         * Run at the end of a pass, after the armor model is rendered.
         * @param stack The ItemStack
         * @param wearer The Entity that has the armor equipped
         * @param slot The ID of the slot that the armor is in
         * @param pass The effect pass
         * @param partialTicks The partialTick value passed to the armor renderer
         * */
        public abstract void postPass(ItemStack stack,EntityLivingBase theWearer, int slot, int pass, float partialTicks);

        /**
         * Returns the required number of passes to run through. Defaults to two for vanilla.<br>
         * Use for initialization.
         * @param stack the ItemStack being rendered
         * */
        public abstract int getPassTarget(ItemStack stack, EntityLivingBase wearer, int slot);
    }

    /*-------------------------Vanilla Encapsulation---------------------------------*/

    /**    
     * Vanilla's ItemStack Enchantment encapsulated in new methodology
     * */
    private static final class VanillaItemPassHandler extends ItemStackEffectPassHandler
    {
        private VanillaItemPassHandler()
        {
            super(true);
        }

        private static final int[] glintColor = new int[2];

        @Override
        public void prePass(ItemStack stack, int pass)
        {
            float f;
            if(pass == 0)
            {
                f = (float)(Minecraft.getSystemTime() % 3000.0F) / 24000.0F;
                GlStateManager.depthMask(false);
                GlStateManager.depthFunc(514);
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scale(8.0F, 8.0F, 8.0F);
                GlStateManager.translate(f, 0.0F, 0.0F);
                GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
                return;
            }
            f = (float)(Minecraft.getSystemTime() % 4873.0F) / 38984.0F;
            GlStateManager.pushMatrix();
            GlStateManager.scale(8.0F, 8.0F, 8.0F);
            GlStateManager.translate(-f, 0.0F, 0.0F);
            GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
        }

        @Override
        public void postPass(ItemStack stack, int pass)
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
            return glintColor[pass];
        }

        @Override
        public int getPassTarget(ItemStack stack) {
            glintColor[0] = stack.getItem().getItemStackEffectColorForPass(stack, 0);    
            glintColor[1] = stack.getItem().getItemStackEffectColorForPass(stack, 1);
            return 2;
        }

        @Override
        public void bindTexture(TextureManager textureAccess)
        {
            textureAccess.bindTexture(RES_DEFAULT_GLINT);
        }

    }
    /**
     * Vanilla's Armor Enchantment encapsulated in new methodology
     * */
    private static final class VanillaArmorPassHandler extends ArmorEffectPassHandler
    {

        private VanillaArmorPassHandler()
        {
            super(true);
        }
        
        private static final int[] glintColor = new int[2];

        @Override
        public void prePass(ItemStack stack,EntityLivingBase wearer, int slot, int pass, float partialTicks)
        {     
            float f = (float)wearer.ticksExisted + partialTicks;
            if(pass == 0)
            {
                GlStateManager.enableBlend();
                GlStateManager.depthFunc(514);
                GlStateManager.depthMask(false);
                GlStateManager.disableLighting();
            }
            GlStateManager.blendFunc(768, 1);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
            setColor(glintColor[pass]);
            GlStateManager.rotate((pass == 0 ? 30F : -30F), 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (pass == 0 ? 6.0000002E-5F : 0.060060006F), 0.0F);
            GlStateManager.matrixMode(5888);
        }

        @Override
        public void postPass(ItemStack stack, EntityLivingBase theWearer, int slot, int pass,float partialTicks)
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
        public int getPassTarget(ItemStack stack, EntityLivingBase wearer, int slot)
        {
            glintColor[0] = stack.getItem().getArmorEffectColorForPass(stack, wearer, slot, 0);    
            glintColor[1] = stack.getItem().getArmorEffectColorForPass(stack, wearer, slot, 1);
            return 2;
        }

        @Override
        public void bindTexture(RendererLivingEntity<? extends EntityLivingBase> textureAccess)
        {
            textureAccess.bindTexture(RES_DEFAULT_GLINT);
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