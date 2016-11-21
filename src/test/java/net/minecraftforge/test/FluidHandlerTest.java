package net.minecraftforge.test;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(modid="fluidhandlertest", name="FluidHandlerTest", version="0.0.0")
public class FluidHandlerTest
{
	public static final boolean ENABLE = true;

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
		ItemStack originalStack = stack.copy();

		ItemStack preDrainStack = stack.copy();
		IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(preDrainStack);
		if (fluidHandler != null)
		{
			FluidStack drain = fluidHandler.drain(Integer.MAX_VALUE, true);
			ItemStack drainedStack = fluidHandler.getContainer();
			FMLLog.info("Draining " + stackString(stack) + " gives " + fluidString(drain) + " and " + stackString(drainedStack));

			if (drain == null && !ItemStack.areItemStacksEqual(originalStack, preDrainStack))
			{
				throw new RuntimeException("ItemStack was altered by its fluid handler when drain did nothing.");
			}

			for (Fluid fluid : FluidRegistry.getRegisteredFluids().values())
			{
				ItemStack preFillStack = stack.copy();
				fluidHandler = FluidUtil.getFluidHandler(preFillStack);
				if (fluidHandler != null)
				{
					int filled = fluidHandler.fill(new FluidStack(fluid, Integer.MAX_VALUE), true);
					ItemStack filledStack = fluidHandler.getContainer();

					if (filled > 0)
					{
						FMLLog.info("Filling " + stackString(stack) + " with " + fluidString(new FluidStack(fluid, filled)) + " gives " + stackString(filledStack));
					}
					else
					{
						if (!ItemStack.areItemStacksEqual(originalStack, preFillStack))
						{
							throw new RuntimeException("ItemStack was altered by its fluid handler when fill did nothing.");
						}
						if (!ItemStack.areItemStacksEqual(preFillStack, filledStack))
						{
							throw new RuntimeException("ItemStack was altered by its fluid handler when fill did nothing.");
						}
					}
				}
			}
		}
	}

	private static String fluidString(@Nullable FluidStack stack)
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

	private static String stackString(@Nonnull ItemStack stack)
	{
		if (stack.func_190926_b())
		{
			return "no item";
		}
		else
		{
			String resourceDomain;
			if (stack.getItem().getRegistryName() == null)
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
