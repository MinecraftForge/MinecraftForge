/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraftforge.eventbus.api.bus.EventBus;
import net.minecraftforge.eventbus.api.event.MutableEvent;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ItemTooltipEvent extends MutableEvent implements PlayerEvent {
    public static final EventBus<ItemTooltipEvent> BUS = EventBus.create(ItemTooltipEvent.class);

    private final Player player;
    private final TooltipFlag flags;
    @NotNull
    private final ItemStack itemStack;
    private final List<Component> toolTip;
    private final Item.TooltipContext context;
    private final TooltipDisplay display;

    /**
     * This event is fired in {@link ItemStack#getTooltipLines(Item.TooltipContext, Player, TooltipFlag)}, which in turn is called from its respective GUIContainer.
     * Tooltips are also gathered with a null player during startup by {@link Minecraft#createSearchTrees()}.
     */
    @ApiStatus.Internal
    public ItemTooltipEvent(@NotNull ItemStack itemStack, @Nullable Player player, List<Component> list, TooltipFlag flags, Item.TooltipContext context, TooltipDisplay display)
    {
        this.player = player;
        this.itemStack = itemStack;
        this.toolTip = list;
        this.flags = flags;
        this.context = context;
        this.display = display;
    }

    /**
     * The {@link net.minecraft.world.item.Item.TooltipContext} for this tooltip.
     */
    public Item.TooltipContext getContext()
    {
        return context;
    }

    /**
     * The {@link net.minecraft.world.item.component.TooltipDisplay} for this tooltip.
     */
    public TooltipDisplay getDisplay()
    {
        return display;
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
        return player;
    }
}
