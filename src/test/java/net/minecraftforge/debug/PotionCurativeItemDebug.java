package net.minecraftforge.debug;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Usage (use survival so you can eat food):
 * 1. Drink curable_potion from Brewing creative tab
 * 2. Relog to test that changes to curative items persist, then eat the "medicine" item to cure the effect
 * 3. Drink incurable_potion from Brewing creative tab
 * 4. Relog to test that changes to curative items persist, then try drinking milk and eating medicine: they should have no effect
 */
@Mod(modid = PotionCurativeItemDebug.MOD_ID)
public class PotionCurativeItemDebug
{
    public static final String MOD_ID = "potion_curative_item_debug";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        Item medicine = new Medicine().setRegistryName(MOD_ID, "medicine");
        GameRegistry.register(medicine);

        Potion incurablePotion = new IncurablePotion().setRegistryName(MOD_ID, "incurable_potion");
        GameRegistry.register(incurablePotion);

        // Register PotionType that can be cured with medicine
        PotionEffect curable = new PotionEffect(incurablePotion, 1200);
        curable.setCurativeItems(Collections.singletonList(new ItemStack(medicine)));
        GameRegistry.register(new PotionType(curable).setRegistryName(MOD_ID, "curable_potion_type"));

        // Register PotionType that can't be cured
        GameRegistry.register(new PotionType(new PotionEffect(incurablePotion, 1200)).setRegistryName(MOD_ID, "incurable_potion_type"));
    }

    private static class IncurablePotion extends Potion
    {
        protected IncurablePotion()
        {
            super(false, 0x94A061);
            setPotionName("incurable_potion");
            setIconIndex(6, 0);
        }

        @Override
        public List<ItemStack> getCurativeItems()
        {
            // By default, no PotionEffect of this Potion can be cured by anything
            return new ArrayList<ItemStack>();
        }
    }

    private static class Medicine extends ItemFood
    {
        public Medicine()
        {
            super(2, 1, false);
            setUnlocalizedName("medicine");
            setAlwaysEdible();
        }

        @Override
        protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player)
        {
            if (!worldIn.isRemote)
            {
                player.curePotionEffects(stack);
            }
        }
    }
}
