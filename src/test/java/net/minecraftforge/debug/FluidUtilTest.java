package net.minecraftforge.debug;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;

@Mod(FluidUtilTest.MODID)
public class FluidUtilTest
{
	public static final String MODID = "fluid_util_test";

	public FluidUtilTest()
	{
		FMLJavaModLoadingContext.get().getModEventBus().addListener(FluidUtilTest::runTests);
	}

	private static void runTests(FMLCommonSetupEvent commonSetupEvent)
	{
		test_tryEmptyContainerAndStow_stackable();

		LogManager.getLogger().info("FluidUtilTest ok!");
	}

	private static void test_tryEmptyContainerAndStow_stackable()
	{
		var sourceStack = new ItemStack(Items.WATER_BUCKET, 2);
		var targetTank = new FluidTank(10000);
		var overflowInventory = new ItemStackHandler(1);

		var result = FluidUtil.tryEmptyContainerAndStow(sourceStack, targetTank, overflowInventory, 1000, null, true);
		var resultStack = result.getResult();

		if (!result.isSuccess())
			throw new AssertionError("Failed to transfer.");
		if (resultStack.getItem() != Items.WATER_BUCKET || resultStack.getCount() != 1)
			throw new AssertionError("Expected 1 water bucket, got: " + resultStack);
		if (targetTank.getFluid().getFluid() != Fluids.WATER || targetTank.getFluidAmount() != 1000)
			throw new AssertionError("Expected 1000 water in target, got: " + targetTank.getFluidAmount() + " of " + targetTank.getFluid().getFluid());
		if (overflowInventory.getStackInSlot(0).getItem() != Items.BUCKET || overflowInventory.getStackInSlot(0).getCount() != 1)
			throw new AssertionError("Expected 1 empty bucket, got: " + overflowInventory.getStackInSlot(0));
	}
}
