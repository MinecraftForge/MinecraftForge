/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.fluid;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.chat.TextComponent;
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
      FluidUtil.getFluidContained(stack).ifPresent((fluid) -> event.getPlayer().displayClientMessage(new TextComponent("Contains ").append(fluid.getDisplayName()), true));
    }
  }
}
