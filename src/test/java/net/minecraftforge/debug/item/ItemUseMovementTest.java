package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ItemUseMovementTest.MODID, name = "Item Use Movement Test", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber(modid = ItemUseMovementTest.MODID)
public class ItemUseMovementTest
{
	public static  final String MODID = "iumtest";
	private static final boolean ENABLED = false;
	public static final Item LIGHTSTEAK = new ItemLightFood(3, false).setAlwaysEdible().setRegistryName("light_steak");

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event)
	{
		if(ENABLED) event.getRegistry().registerAll(LIGHTSTEAK);
	}

	@SubscribeEvent
	public static void registerModels(ModelRegistryEvent event)
	{
		if(ENABLED) ModelLoader.setCustomModelResourceLocation(LIGHTSTEAK, 0, new ModelResourceLocation("minecraft:cooked_beef"));
	}

	private static class ItemLightFood extends ItemFood
	{
		public ItemLightFood(int amount, boolean isWolfFood)
		{
			super(amount, isWolfFood);
		}

		@Override
		public float getMovementSpeedMultiplier(ItemStack stack, EntityLivingBase entity)
		{
			return 1F;
		}

		@Override
		public boolean shouldPreventSprinting(ItemStack stack, EntityLivingBase entity)
		{
			return false;
		}
	}
}