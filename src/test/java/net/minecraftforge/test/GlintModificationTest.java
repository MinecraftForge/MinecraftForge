package net.minecraftforge.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.EffectOverlayEvent;
import net.minecraftforge.client.model.pipeline.EffectPassHandler.ItemStackEffectPassHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="glintTest", clientSideOnly = true)
public class GlintModificationTest 
{
    private static final ResourceLocation EMBER = new ResourceLocation("forgedebugglint", "textures/misc/embers.png");
    
    @EventHandler
    public void pre(FMLPreInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        GameRegistry.registerItem(new ItemArmor(ArmorMaterial.LEATHER, 0, 0)
        {
        @Override
        public int getItemStackEffectColorForPass(ItemStack i, int p){
            return p == 0 ? 0xCC22FF99 : 0xCC2299FF;
        }
    
        @Override
        public int getArmorEffectColorForPass(ItemStack i, EntityLivingBase e, int s, int p)
        {
            return p == 0 ? 0xFFFF0000  : 0xCC22FF99;
        }
    
        @Override
        public boolean isValidArmor(ItemStack i, int slot, Entity e)
        {
            return slot == 0;
        }

        @Override
        public boolean hasEffect(ItemStack i){
            return true;
        }
    
        }.setUnlocalizedName("glint_test").setRegistryName("glint_test"));
    }

    @SubscribeEvent
    public void doTheItems(EffectOverlayEvent.ItemStackEffectOverlayEvent e)
    {
        if(e.getStack() == null)
            return;
        if(e.getStack().getItem() instanceof ItemPotion)
            e.setGlintColor(0x41 << 24 | (((ItemPotion)e.getStack().getItem()).getColorFromDamage(e.getStack().getItemDamage())), 0xFFcccccc);
        else if(e.getStack().getItem() instanceof ItemEnchantedBook && e.getStack().hasTagCompound())
            e.setCanceled(true);
        else if(e.getStack().getItem() instanceof ItemSword && e.getStack().isItemEnchanted() && EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, e.getStack()) > 0)
            e.setPassHandler(EmberSwordPassHandler.instance);
    }
    
    @SubscribeEvent
    public void doTheArmors(EffectOverlayEvent.ArmorEffectOverlayEvent e)
    {
        if(e.getArmorSlotID() == 2)
            e.setGlintColor(0xffcccc00, 0xFFFF0000);
    }
    
    private static final class EmberSwordPassHandler extends ItemStackEffectPassHandler
    {
        
        public static final int[] color = {0xFFFFFF00, 0xFFFF0000, 0xFFFFCC00, 0xFFCC0000};
 
        public static EmberSwordPassHandler instance = new EmberSwordPassHandler();

        private EmberSwordPassHandler()
        {
            super(4);
        }

        @Override
        public void prePass(ItemStack theStack, TextureManager textureManager, int pass)
        {
            float f = 15873L - (1873L * pass);
            f = (float)(Minecraft.getSystemTime() % f) / f/ ((pass + 1) / 2f);
            if(pass == 0)
            {
                GlStateManager.depthMask(false);
                GlStateManager.depthFunc(514);
                GlStateManager.enableBlend();
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                textureManager.bindTexture(EMBER);
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scale(8.0F, 8.0F, 8.0F);
                GlStateManager.translate(f, pass, 0.0F);
            }else
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(44.0F/pass, 44.0F/pass, 44.0F);
                GlStateManager.translate(-f, f/2, pass/f);
            }
        }

        @Override
        public void postPass(ItemStack theStack, TextureManager textureManager, int pass)
        {
            GlStateManager.popMatrix();
            if(pass == 3)
            {
                GlStateManager.matrixMode(5888);
                GlStateManager.blendFunc(770, 771);
                GlStateManager.enableLighting();
                GlStateManager.depthFunc(515);
                GlStateManager.depthMask(true);
            }          
        }

        @Override
        public int getColorForPass(ItemStack theStack, int pass)
        {
            return color[pass];
        }

    }

}
