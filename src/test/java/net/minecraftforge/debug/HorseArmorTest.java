package net.minecraftforge.debug;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = HorseArmorTest.MODID, name = "HorseArmorTest", version = "1.0")
public class HorseArmorTest 
{
	public static final String MODID = "horse_armor_test";
	public static final boolean ENABLED = false;
	
	@SidedProxy
	public static CommonProxy proxy;
	
	public static HorseArmorType testArmorType;
	public static ItemTestHorseArmor testArmorItem = new ItemTestHorseArmor();
	
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		if(ENABLED)
		{
			proxy.preInit(event);
		}
	}
	
	public static abstract class CommonProxy 
	{
		public void preInit(FMLPreInitializationEvent event)
		{
			testArmorType = EnumHelper.addHorseArmor("test", MODID + ":textures/entity/horse/armor/test.png", "tst", 15);
			testArmorItem.setRegistryName(MODID, "test_armor");
			testArmorItem.setUnlocalizedName(MODID + ".testArmor");
			GameRegistry.register(testArmorItem);
		}
	}
	
	public static class ClientProxy extends CommonProxy 
	{
		@Override
		public void preInit(FMLPreInitializationEvent event) 
		{
			super.preInit(event);
			ModelLoader.setCustomModelResourceLocation(testArmorItem, 0, new ModelResourceLocation(testArmorItem.getRegistryName(), "inventory"));
		}
	}
	
	public static class ServerProxy extends CommonProxy {}
	
	private static class ItemTestHorseArmor extends Item
	{
		@Override
		public boolean isValidHorseArmor(ItemStack stack) 
		{
			return true;
		}
		
		@Override
		public HorseArmorType getHorseArmorType(ItemStack stack) 
		{
			return testArmorType;
		}
		
		@Override
		public void onHorseArmorTick(World world, EntityHorse horse, ItemStack itemStack) 
		{
			if(horse.ticksExisted % 15 == 0)
				horse.addPotionEffect(new PotionEffect(MobEffects.GLOWING, 20, 1));
		}
	}
}
