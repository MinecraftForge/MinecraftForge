/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.fluid;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.common.Mod;

@Mod(MilkFluidTest.MODID)
public class MilkFluidTest
{
  protected static final String MODID = "milk_fluid_test";
  private static final boolean ENABLE = false;

  public MilkFluidTest()
  {
    if (ENABLE)
    {
      ForgeMod.enableMilkFluid();
      MinecraftForge.EVENT_BUS.addListener(MilkFluidTest::useMilk);
    }
  }

  private static void useMilk(PlayerInteractEvent event)
  {
    ItemStack stack = event.getItemStack();
    if (stack.getItem() == Items.MILK_BUCKET)
    {
      FluidUtil.getFluidContained(stack).ifPresent((fluid) -> event.getPlayer().displayClientMessage(new StringTextComponent("Contains ").append(fluid.getDisplayName()), true));
    }
  }
}
