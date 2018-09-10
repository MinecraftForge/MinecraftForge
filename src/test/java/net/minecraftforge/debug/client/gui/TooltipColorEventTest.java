/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.client.gui;

import net.minecraft.init.Items;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = TooltipColorEventTest.MODID, name = "Tooltip Color Test", version = "0.1", clientSideOnly = true)
public class TooltipColorEventTest
{
    public static final String MODID = "tooltipcolortest";

    private static final boolean ENABLE = false;

    public TooltipColorEventTest()
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void getTooltipColor(RenderTooltipEvent.Color event)
    {
        if (event.getStack().getItem() == Items.APPLE)
        {
            event.setBackground(0xF0510404);
            event.setBorderStart(0xF0bc0909);
            event.setBorderEnd(0xF03f0f0f);
        }
    }
}