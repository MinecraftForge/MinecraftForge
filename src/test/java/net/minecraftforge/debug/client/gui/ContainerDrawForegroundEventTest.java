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

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
@Mod (modid = "guicontainereventtest", name = "GuiContainer Event Tests!", version = "1.0", acceptableRemoteVersions = "*")
public class ContainerDrawForegroundEventTest
{
    static final boolean ENABLED = false;

    @ObjectHolder("minecraft:stone")
    public static final Item STONE_ITEM = null;

    @SubscribeEvent
    public static void onForegroundRender(GuiContainerEvent.DrawForeground event)
    {
        if (!ENABLED) return;

        for (Slot slot : event.getGuiContainer().inventorySlots.inventorySlots)
        {
            if (slot.getStack().getItem() == STONE_ITEM)
            {
                GlStateManager.disableLighting();

                GuiUtils.drawGradientRect(400, slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, 0x80000000, 0x80000000);

                GlStateManager.enableLighting();
            }
        }
    }

}
