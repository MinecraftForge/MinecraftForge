package net.minecraftforge.test;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.pipeline.EffectPassHandler.ArmorEffectPassHandler;
import net.minecraftforge.client.model.pipeline.EffectPassHandler.ItemStackEffectPassHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="glintTest", clientSideOnly = true)
public class GlintModificationTest 
{
    private static final ResourceLocation EMBER = new ResourceLocation("forgedebugglint", "textures/misc/embers.png");
    
    private static Item item;
    
    @EventHandler
    public void pre(FMLPreInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        item = new ItemArmor(ArmorMaterial.LEATHER, 0, 0)
        {
        @Override
        public int getItemStackEffectColorForPass(ItemStack i, int p){
            return p == 0 ? 0xCC22FF99 : 0xCCFF9900;
        }
    
        @Override
        public int getArmorEffectColorForPass(ItemStack stack, EntityLivingBase e, int s, int p)
        {
            return p == 0 ? 0xFF22FFFF  : 0xCCFF0000;
        }
    
        @Override
        public boolean isValidArmor(ItemStack stack, int slot, Entity entity)
        {
            return slot == 0;
        }

        @Override
        public boolean hasEffect(ItemStack i)
        {
            return true;
        }
        
        @Override
        public ItemStackEffectPassHandler getEffectRendererForItemStack(ItemStack stack){
            return stack.getItemDamage() == 0 ? EmberItem.instance : EmberItem.vanillaStackPassHandler;
        }

        @Override
        public ArmorEffectPassHandler getEffectRendererForArmor(ItemStack stack){
            return stack.getItemDamage() == 0 ? EmberArmor.instance : EmberArmor.vanillaArmorPassHandler;
        }

        @Override
        public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
            super.getSubItems(item, tab, list);
            list.add(new ItemStack(item, 1, 1));
        }
    
        }.setUnlocalizedName("glint_test").setRegistryName("glint_test");
        GameRegistry.registerItem(item);
    }
    
    @EventHandler
    public void peri(FMLInitializationEvent event)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation("bottle_drinkable", "inventory"));
    }
    
    private static final class EmberItem extends ItemStackEffectPassHandler
    {
        
        public static final int[] color = {-1, 0xFFFF0000, 0xFFFFCC00, 0xFFCC0000};
 
        public static EmberItem instance = new EmberItem();

        private EmberItem()
        {
            super(false);
        }

        @Override
        public void prePass(ItemStack theStack, int pass)
        {
            float f = 15873L - (1873L * pass);
            f = (float)(Minecraft.getSystemTime() % f) / f/ ((pass + 1) / 2f);
            if(pass == 1)
            {
                GlStateManager.depthMask(false);
                GlStateManager.depthFunc(514);
                GlStateManager.enableBlend();
                GlStateManager.disableLighting();
                GlStateManager.blendFunc(768, 1);
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scale(8.0F, 8.0F, 8.0F);
                GlStateManager.translate(f, pass, 0.0F);
                return;
            }
            GlStateManager.pushMatrix();
            GlStateManager.scale(44.0F/pass, 44.0F/pass, 44.0F);
            GlStateManager.translate(-f, f/2, pass/f);
        }

        @Override
        public void postPass(ItemStack theStack, int pass)
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

        @Override
        public int getPassTarget(ItemStack stack)
        {
            return 4;
        }

        @Override
        public void bindTexture(TextureManager textureAccess)
        {
            textureAccess.bindTexture(EMBER);
        }

    }

    private static final class EmberArmor extends ArmorEffectPassHandler
    {
        
        public static final int[] color = {0xFFFFFF00, 0xFFFF0000};
 
        public static EmberArmor instance = new EmberArmor();

        private EmberArmor()
        {
            super(false);
        }

        @Override
        public void prePass(ItemStack stack, EntityLivingBase wearer, int slot, int pass, float partialTicks)
        {
            float f = ((float)wearer.ticksExisted  % 300F / (partialTicks + pass));
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
            setColor(color[pass]);
            GlStateManager.scale(8.0F, 8.0F, 8.0F);
            GlStateManager.translate(0, f, f);
            GlStateManager.matrixMode(5888);
        }

        @Override
        public void postPass(ItemStack theStack, EntityLivingBase theWearer, int slot, int pass, float partialTicks)
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
            return 2;
        }

        @Override
        public void bindTexture(RendererLivingEntity<? extends EntityLivingBase> textureAccess)
        {
            textureAccess.bindTexture(EMBER);
        }

    }
}
