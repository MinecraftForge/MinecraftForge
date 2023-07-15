/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;

/**
 * Various tests for {@link FluidUtil}, that run when the mod is loaded.
 * If one of the tests fails, an expection will be thrown, and mod loading will fail with an error.
 * If all tests pass, the mod will load successfully.
 */
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
		test_tryEmptyContainer();
		test_tryFillContainer();
		test_tryEmptyContainerAndStow_stackable();

		LogManager.getLogger().info("FluidUtilTest ok!");
	}

	/**
	 * Ensures that tryEmptyContainer doesn't change the target fluid handler when simulating.
	 * Regression test for the root cause of <a href="https://github.com/MinecraftForge/MinecraftForge/issues/6796">issue #6796</a>.
	 */
	private static void test_tryEmptyContainer()
	{
		var sourceStack = new ItemStack(Items.WATER_BUCKET, 2);
		var targetTank = new FluidTank(10000);

		// Simulate is not supposed to modify anything
		var simulateResult = FluidUtil.tryEmptyContainer(sourceStack, targetTank, 1000, null, false);
		if (!simulateResult.isSuccess())
			throw new AssertionError("Failed to transfer.");
		checkItemStack(simulateResult.getResult(), Items.BUCKET, 1);
		// Tank and stack shouldn't be modified
		checkItemStack(sourceStack, Items.WATER_BUCKET, 2);
		checkFluidStack(targetTank.getFluid(), Fluids.EMPTY, 0);

		// Execute should modify
		var executeResult = FluidUtil.tryEmptyContainer(sourceStack, targetTank, 1000, null, true);
		if (!executeResult.isSuccess())
			throw new AssertionError("Failed to transfer.");
		checkItemStack(executeResult.getResult(), Items.BUCKET, 1);
		checkFluidStack(targetTank.getFluid(), Fluids.WATER, 1000);
		checkItemStack(sourceStack, Items.WATER_BUCKET, 2); // Apparently the stack is not supposed to be modified
	}

	/**
	 * Ensures that tryFillContainer doesn't change the target fluid handler when simulating.
	 * Ant that the result of the simulated transfver is valid.
	 * Regression test for the root cause of <a href="https://github.com/MinecraftForge/MinecraftForge/issues/6796">issue #6796</a>.
	 */
	private static void test_tryFillContainer()
	{
		var targetStack = new ItemStack(Items.BUCKET, 2);
		var sourceTank = new FluidTank(10000);
		sourceTank.setFluid(new FluidStack(Fluids.WATER, 5000));

		// Simulate is not supposed to modify anything
		var simulateResult = FluidUtil.tryFillContainer(targetStack, sourceTank, 1000, null, false);
		if (!simulateResult.isSuccess())
			throw new AssertionError("Failed to transfer.");
		checkItemStack(simulateResult.getResult(), Items.WATER_BUCKET, 1);
		// Tank and stack shouldn't be modified
		checkItemStack(targetStack, Items.BUCKET, 2);
		checkFluidStack(sourceTank.getFluid(), Fluids.WATER, 5000);

		// Execute should modify
		var executeResult = FluidUtil.tryFillContainer(targetStack, sourceTank, 1000, null, true);
		if (!executeResult.isSuccess())
			throw new AssertionError("Failed to transfer.");
		checkItemStack(executeResult.getResult(), Items.WATER_BUCKET, 1);
		checkFluidStack(sourceTank.getFluid(), Fluids.WATER, 4000);
		checkItemStack(targetStack, Items.BUCKET, 2);
	}

	/**
	 * Ensures that tryEmptyContainerAndStow doesn't duplicate fluids in the target when the container is stackable.
	 * Regression test for <a href="https://github.com/MinecraftForge/MinecraftForge/issues/6796">issue #6796</a>.
	 */
	private static void test_tryEmptyContainerAndStow_stackable()
	{
		var sourceStack = new ItemStack(Items.WATER_BUCKET, 2);
		var targetTank = new FluidTank(10000);
		var overflowInventory = new ItemStackHandler(1);

		// Simulate first: it's not supposed to modify anything!
		var simulateResult = FluidUtil.tryEmptyContainerAndStow(sourceStack, targetTank, overflowInventory, 1000, null, false);
		if (!simulateResult.isSuccess())
			throw new AssertionError("Failed to transfer.");
		checkItemStack(simulateResult.getResult(), Items.WATER_BUCKET, 1);
		// Tank and inv shouldn't be modified for simulate
		checkItemStack(sourceStack, Items.WATER_BUCKET, 2);
		checkFluidStack(targetTank.getFluid(), Fluids.EMPTY, 0);
		checkItemStack(overflowInventory.getStackInSlot(0), Items.AIR, 0);

		// Now test with execute
		var executeResult = FluidUtil.tryEmptyContainerAndStow(sourceStack, targetTank, overflowInventory, 1000, null, true);
		if (!executeResult.isSuccess())
			throw new AssertionError("Failed to transfer.");
		checkItemStack(executeResult.getResult(), Items.WATER_BUCKET, 1);
		checkFluidStack(targetTank.getFluid(), Fluids.WATER, 1000);
		checkItemStack(overflowInventory.getStackInSlot(0), Items.BUCKET, 1);
	}

	private static void checkItemStack(ItemStack stack, Item item, int count)
	{
		if (stack.getItem() != item)
			throw new AssertionError("Expected item " + ForgeRegistries.ITEMS.getKey(item) + ", got: " + ForgeRegistries.ITEMS.getKey(stack.getItem()));
		if (stack.getCount() != count)
			throw new AssertionError("Expected count " + count + ", got: " + stack.getCount());
	}

	private static void checkFluidStack(FluidStack stack, Fluid fluid, int amount)
	{
		if (stack.getFluid() != fluid)
			throw new AssertionError("Expected fluid " + ForgeRegistries.FLUIDS.getKey(fluid) + ", got: " + ForgeRegistries.FLUIDS.getKey(stack.getFluid()));
		if (stack.getAmount() != amount)
			throw new AssertionError("Expected amount " + amount + ", got: " + stack.getAmount());
	}
}
