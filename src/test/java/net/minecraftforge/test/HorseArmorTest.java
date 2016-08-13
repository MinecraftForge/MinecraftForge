package net.minecraftforge.test;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IHorseArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Test mod for IHorseArmor
 */
@Mod(modid = HorseArmorTest.MODID, name = HorseArmorTest.MODNAME, version = "0.0.0")
public class HorseArmorTest 
{
    public static final String MODID = "forgedebughorsearmor";
    public static final String MODNAME = "Horse Armor Test";
    private static final boolean ENABLED = false;
    private static final Logger LOG = LogManager.getLogger(MODNAME);
    private static final HorseArmorType TEST_MATERIAL = EnumHelper.addArmorType("FORGE_TEST", 23, MODID + ":textures/entity/horse/test_armor.png", "forge_test");

    @EventHandler
    public void init(FMLPreInitializationEvent event) 
    {
        if (ENABLED) 
        {
            GameRegistry.register(new ItemTestHorseArmor());
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onEntityHit(LivingHurtEvent event) 
    {
        if (event.getEntity() instanceof EntityHorse)
        {
            // Armor is limited to 30 by Vanilla
            LOG.info("Total Armor: " + event.getEntityLiving().getTotalArmorValue());
        }
    }

    public class ItemTestHorseArmor extends Item implements IHorseArmor 
    {
        public ItemTestHorseArmor() 
        {
            this.setRegistryName("test_armor");
            this.setUnlocalizedName(MODID + ".testarmor");
            this.setCreativeTab(CreativeTabs.MISC);
            this.setMaxStackSize(1);
        }

        @Override
        public HorseArmorType getArmorType(ItemStack stack) 
        {
            return TEST_MATERIAL;
        }

        @Override
        public String getArmorTexture(EntityLivingBase wearer, ItemStack stack) 
        {
            return MODID + ":textures/entity/horse/test_armor.png";
        }

        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) 
        {
            tooltip.add("Protection: " + this.getArmorType(stack).getProtection());
        }
    }
}
