package net.minecraftforge.debug;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ArmorOverlayHandler;
import net.minecraftforge.client.IArmorOverlay;
import net.minecraftforge.client.IItemOverlay;
import net.minecraftforge.client.ItemOverlayHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid="effectoverlaytest", clientSideOnly = true)
public class EffectOverlayTest {

    public static final ResourceLocation EMBER = new ResourceLocation("effectoverlaytest", "textures/misc/embers.png");

    @EventHandler
    public void peri(FMLInitializationEvent event)
    {
        ItemOverlayHandler.registerOverlayHandler(testItem, Items.LEATHER_CHESTPLATE);
        ArmorOverlayHandler.registerOverlayHandler(testArmor, Items.LEATHER_CHESTPLATE);
    }
    
    private static final IItemOverlay testItem = new IItemOverlay()
    {

        @Override
        public int getOverlayColor(ItemStack stack, int pass)
        {
            return pass == 0 ? 0xFF113311 : 0xFFCC1100;
        }

        @Override
        public long getOverlayRepeat(ItemStack stack, int pass)
        {
            return pass == 0 ? 8000L : 9873L;
        }

        @Override
        public float getOverlaySpeed(ItemStack stack, int pass)
        {
            return pass == 0 ? 150000.0F : 177984.0F;
        }

        @Override
        public float[] getOverlayScaleVector(ItemStack stack, int pass, float time)
        {
            return new float[] {12.0F, 12.0F, 12.0F};
        }

        @Override
        public float[] getOverlayTranslationVector(ItemStack stack, int pass, float time)
        {
            return pass == 0 ? new float[] {time, 0.0F, 0.0F} : new float[] {-time, 0.0F, 0.0F};
        }

        @Override
        public float[] getOverlayRotationVector(ItemStack stack, int pass, float time)
        {
            return pass == 0 ? new float[] {150.0F, 0.0F, 0.0F, 1.0F} : new float[] {190.0F, 0.0F, 0.0F, 1.0F};
        }

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack)
        {
            return EMBER;
        }
        
    };

    private static final IArmorOverlay testArmor = new IArmorOverlay()
    {

        @Override
        public int getOverlayColor(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot)
        {
            return pass == 0 ? 0xFF119999  : 0xCCFF0000;
        }

        @Override
        public float[] getOverlayScaleVector(ItemStack stack,EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time)
        {
            return new float[] {0.33333334F, 0.33333334F, 0.33333334F};
        }

        @Override
        public float[] getOverlayTranslationVector(ItemStack stack,EntityLivingBase wearer, int pass, EntityEquipmentSlot slot,float time)
        {
            return new float[] { time * (pass == 0 ? 0.1003F : 0.03003F), 0.0F, 0.0F};
        }

        @Override
        public float[] getOverlayRotationVector(ItemStack stack, EntityLivingBase wearer, int pass, EntityEquipmentSlot slot, float time)
        {
            return new float[] {(pass == 0 ? 120F : 60F), 0.0F, 0.0F, 1.0F};
        }

        @Override
        public ResourceLocation getOverlayTexture(ItemStack stack,EntityLivingBase wearer)
        {
            return EMBER;
        }
        
    };

}
