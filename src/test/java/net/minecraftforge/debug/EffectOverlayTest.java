package net.minecraftforge.debug;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="effectoverlaytest", clientSideOnly = true)
public class EffectOverlayTest {
    public static final ResourceLocation EMBER = new ResourceLocation("effectoverlaytest", "textures/misc/embers.png");
    
    private static Item item;
    
    @EventHandler
    public void pre(FMLPreInitializationEvent e)
    {
        MinecraftForge.EVENT_BUS.register(this);
        item = new ItemArmor(ArmorMaterial.LEATHER, 0, EntityEquipmentSlot.HEAD)
        {
        @Override
        public int getItemEffectColorForPass(ItemStack i, int p){
            return p == 0 ? 0xCC22FF99 : 0xCCFF9900;
        }
    
        @Override
        public int getArmorEffectColorForPass(ItemStack stack, EntityLivingBase e,int p, EntityEquipmentSlot s)
        {
            return p == 0 ? 0xCC22FFFF  : 0xCCFF0000;
        }

        @Override
        public boolean hasEffect(ItemStack i)
        {
            return true;
        }

        @Override
        public long getItemEffectTimeRepeatForPass(ItemStack stack, int pass)
        {
            return pass == 0 ? 8000L : 9873L;
        }

        @Override
        public float getItemEffectSpeed(ItemStack stack, int pass)
        {
            return pass == 0 ? 150000.0F : 177984.0F;
        }

        @Override
        public float[] getItemEffectScaleVectorForPass(ItemStack stack, int pass, float time)
        {
            return new float[] {12.0F, 12.0F, 12.0F};
        }

        @Override
        public float[] getItemEffectTranslationVectorForPass(ItemStack stack, int pass, float time)
        {
            return pass == 0 ? new float[] {time, 0.0F, 0.0F} : new float[] {-time, 0.0F, 0.0F};
        }

        @Override
        public float[] getItemEffectRotationVectorForPass(ItemStack stack, int pass, float time)
        {
            return pass == 0 ? new float[] {150.0F, 0.0F, 0.0F, 1.0F} : new float[] {190.0F, 0.0F, 0.0F, 1.0F};
        }

        @Override
        public ResourceLocation getItemEffectTexture(ItemStack stack)
        {
            return EMBER;
        }

        @Override
        public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> list){
            super.getSubItems(item, tab, list);
        }

        @Override
        public ResourceLocation getArmorEffectTexture(ItemStack stack, EntityLivingBase wearer)
        {
            return EMBER;
        }

        public float[] getArmorEffectScaleVectorForPass(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time)
        {
            return new float[] {0.33333334F, 0.33333334F, 0.33333334F};
        }

        public float[] getArmorEffectTranslationVectorForPass(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time)
        {
            return new float[] { time * (pass == 0 ? 0.1003F : 0.03003F), 0.0F, 0.0F};
        }

        public float[] getArmorEffectRotationVectorForPass(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time)
        {
            return new float[] {(pass == 0 ? 120F : 60F), 0.0F, 0.0F, 1.0F};
        }

        }.setUnlocalizedName("glint_test").setRegistryName("glint_test");
        GameRegistry.registerItem(item);
    }
    
    @EventHandler
    public void peri(FMLInitializationEvent event)
    {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation("bottle_drinkable", "inventory"));
    }
    

}
