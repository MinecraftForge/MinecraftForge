package net.minecraftforge.test;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid="fluidhandlertest", name="FluidHandlerTest", version="0.0.0")
public class FluidHandlerTest
{
	public static final boolean ENABLE = false;

	@Mod.EventHandler
	public void loadComplete(FMLLoadCompleteEvent event)
	{
		if (!ENABLE || FMLCommonHandler.instance().getSide() != Side.CLIENT)
			return;

		for (ItemStack stack : getAllItems())
		{
			testFluidContainer(stack);
		}
	}

	private static void testFluidContainer(ItemStack stack)
	{
		ItemStack drainedStack = stack.copy();
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(drainedStack);
		if (fluidHandler != null)
		{
			FluidStack drain = fluidHandler.drain(Integer.MAX_VALUE, true);
			FMLLog.info("Draining " + stackString(stack) + " gives " + fluidString(drain) + " and " + stackString(drainedStack));

			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
			{
				ItemStack filledStack = stack.copy();
				fluidHandler = FluidUtil.getFluidHandler(filledStack);
				if (fluidHandler != null)
				{
					int filled = fluidHandler.fill(new FluidStack(fluid, Integer.MAX_VALUE), true);
					if (filled > 0)
					{
						FMLLog.info("Filling " + stackString(stack) + " with " + fluidString(new FluidStack(fluid, filled)) + " gives " + stackString(filledStack));
					}
				}
			}
		}
	}

	private static String fluidString(FluidStack stack)
	{
		if (stack == null)
		{
			return "no fluid";
		}
		else
		{
			return stack.amount + "mB " + stack.getLocalizedName();
		}
	}

	private static String stackString(ItemStack stack)
	{
		if (stack.func_190926_b())
		{
			return "no item";
		}
		else
		{
			String resourceDomain;
			if (stack.getItem() == null || stack.getItem().getRegistryName() == null)
			{
				resourceDomain = "unknown";
			}
			else
			{
				resourceDomain = stack.getItem().getRegistryName().getResourceDomain();
			}
			return stack.func_190916_E() + " " + stack.getDisplayName() + " (" + resourceDomain + ")";
		}
	}

	private static List<ItemStack> getAllItems()
	{
		NonNullList<ItemStack> list = NonNullList.func_191196_a();
		for (Item item : ForgeRegistries.ITEMS.getValues())
		{
			for (CreativeTabs tab : item.getCreativeTabs())
			{
				item.getSubItems(item, tab, list);
			}
		}
		return list;
	}
}
