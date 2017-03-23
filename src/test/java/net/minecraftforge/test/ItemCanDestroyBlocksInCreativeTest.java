package net.minecraftforge.test;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ItemCanDestroyBlocksInCreativeTest.MODID, name = "Item.canDestroyBlocksInCreative() Test", version = "1.0", acceptableRemoteVersions = "*")
public class ItemCanDestroyBlocksInCreativeTest{
	public static final boolean ENABLE = true;
	public static final String MODID = "itemcandestroyblocksincreativetest";

	public static Item testItem = new Item(){
		@Override
		public boolean canDestroyBlocksInCreative(){
			return false;
		}
	}.setRegistryName(MODID, "testItem")
	.setUnlocalizedName(MODID + ".testItem")
	.setCreativeTab(CreativeTabs.TOOLS);

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event){
		GameRegistry.register(testItem);
	}
}