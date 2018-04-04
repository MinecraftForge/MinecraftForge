package net.minecraftforge.debug.item;

import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IElytra;

@Mod.EventBusSubscriber(modid = CustomElytraItemTest.MODID)
@Mod(modid = CustomElytraItemTest.MODID, name = "CustomElytraItemTest", version = "0.0.0", acceptableRemoteVersions = "*")
public class CustomElytraItemTest{
    
	public static final String MODID = "custom_elytra_item_test";
	
	public static class TestItem extends Item implements IElytra
	{
		public static Item testItem = new TestItem();
		
		@Override
		public boolean doesSlowOnUpwardsFlight(ItemStack stack, World world)
        {
			//Can fly upwards without slowing down
			return false;
		}

		@Override
		public boolean doesDamageWhileFlying(ItemStack stack, World world)
		{
			//Doesn't use durability upon flying
			return false;
		}

		@Override
		public boolean doesDamageUponHittingWall(ItemStack stack, World world)
		{
			//Doesn't deal damage when hitting a wall
			return false;
		}

		@Override
		public boolean checkFlight(ItemStack stack, World world)
		{
			//This just acts like a regular Elytra, but you could put custom fuel checks or something
			return true;
		}

		@Override
		public float getAccelerationMultiplier(ItemStack stack, World world)
		{
			//Goes a bit faster than normal. This escalates QUICKLY; it can be changed as you use it, but I'd recommend sticking to 1 for most things.
			return 1.1F;
		}

		@Override
		public void onHitWall(ItemStack stack, World world, BlockPos pos)
		{
			//I could put particle effects, explosions, fuel reductions, or really anything.
		}
	};
	
	
	
    
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
                event.getRegistry().register(TestItem.testItem.setRegistryName("custom_elytra_item").setUnlocalizedName("CustomElytraItem").setMaxStackSize(1));
        }
    

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
                ModelBakery.registerItemVariants(TestItem.testItem);
        }
    }
}