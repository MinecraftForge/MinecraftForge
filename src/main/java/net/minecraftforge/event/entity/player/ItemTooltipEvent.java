/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.event.entity.player;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemTooltipEvent extends PlayerEvent
{
    private final boolean showAdvancedItemTooltips;
    private final ItemStack itemStack;
    private final List<String> toolTip;

    /**
     * This event is fired in {@link ItemStack#getTooltip(EntityPlayer, boolean)}, which in turn is called from it's respective GUIContainer.
     */
    public ItemTooltipEvent(ItemStack itemStack, EntityPlayer entityPlayer, List<String> toolTip, boolean showAdvancedItemTooltips)
    {
        super(entityPlayer);
        this.itemStack = itemStack;
        this.toolTip = toolTip;
        this.showAdvancedItemTooltips = showAdvancedItemTooltips;
    }

    /**
     * Whether the advanced information on item tooltips is being shown, toggled by F3+H.
     */
    public boolean isShowAdvancedItemTooltips()
    {
        return showAdvancedItemTooltips;
    }

    /**
     * The {@link ItemStack} with the tooltip.
     */
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * The {@link ItemStack} tooltip.
     */
    public List<String> getToolTip()
    {
        return toolTip;
    }
}