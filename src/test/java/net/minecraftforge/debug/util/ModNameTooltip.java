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

package net.minecraftforge.debug.util;

import javax.annotation.Nullable;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "forgemodnametooltip", name = "ForgeModNameTooltip", version = "1.0", clientSideOnly = true)
@Mod.EventBusSubscriber(Side.CLIENT)
public class ModNameTooltip
{
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onToolTip(ItemTooltipEvent event)
    {
        ItemStack itemStack = event.getItemStack();
        String modName = getModName(itemStack);
        if (modName != null)
        {
            List<String> toolTip = event.getToolTip();
            toolTip.add(TextFormatting.BLUE.toString() + TextFormatting.ITALIC.toString() + modName);
        }
    }

    @Nullable
    private static String getModName(ItemStack itemStack)
    {
        if (!itemStack.isEmpty())
        {
            Item item = itemStack.getItem();
            String modId = item.getCreatorModId(itemStack);
            ModContainer modContainer = Loader.instance().getIndexedModList().get(modId);
            if (modContainer != null)
            {
                return modContainer.getName();
            }
        }
        return null;
    }
}
