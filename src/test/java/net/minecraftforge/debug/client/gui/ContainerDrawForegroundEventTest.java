/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

//@EventBusSubscriber(Side.CLIENT)
//@Mod(modid = "guicontainereventtest", name = "GuiContainer Event Tests!", version = "1.0", acceptableRemoteVersions = "*")
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
*/
