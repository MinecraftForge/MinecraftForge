/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemTooltipEvent extends PlayerEvent
{
    private final TooltipFlag flags;
    @NotNull
    private final ItemStack itemStack;
    private final List<Component> toolTip;

    /**
     * This event is fired in {@link ItemStack#getTooltipLines(Player, TooltipFlag)}, which in turn is called from its respective GUIContainer.
     * Tooltips are also gathered with a null player during startup by {@link Minecraft#createSearchTrees()}.
     */
    public ItemTooltipEvent(@NotNull ItemStack itemStack, @Nullable Player player, List<Component> list, TooltipFlag flags)
    {
        super(player);
        this.itemStack = itemStack;
        this.toolTip = list;
        this.flags = flags;
    }

    /**
     * Use to determine if the advanced information on item tooltips is being shown, toggled by F3+H.
     */
    public TooltipFlag getFlags()
    {
        return flags;
    }

    /**
     * The {@link ItemStack} with the tooltip.
     */
    @NotNull
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * The {@link ItemStack} tooltip.
     */
    public List<Component> getToolTip()
    {
        return toolTip;
    }

    /**
     * This event is fired with a null player during startup when populating search trees for tooltips.
     */
    @Override
    @Nullable
    public Player getEntity()
    {
        return super.getEntity();
    }
}
