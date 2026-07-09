/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemTooltipEvent extends PlayerEvent
{
    private final ITooltipFlag flags;
    @Nonnull
    private final ItemStack itemStack;
    private final List<ITextComponent> toolTip;

    /**
     * This event is fired in {@link ItemStack#getTooltip(EntityPlayer, ITooltipFlag)}, which in turn is called from it's respective GUIContainer.
     * Tooltips are also gathered with a null entityPlayer during startup by {@link Minecraft#populateSearchTreeManager()}.
     */
    public ItemTooltipEvent(@Nonnull ItemStack itemStack, @Nullable PlayerEntity entityPlayer, List<ITextComponent> list, ITooltipFlag flags)
    {
        super(entityPlayer);
        this.itemStack = itemStack;
        this.toolTip = list;
        this.flags = flags;
    }

    /**
     * Use to determine if the advanced information on item tooltips is being shown, toggled by F3+H.
     */
    public ITooltipFlag getFlags()
    {
        return flags;
    }

    /**
     * The {@link ItemStack} with the tooltip.
     */
    @Nonnull
    public ItemStack getItemStack()
    {
        return itemStack;
    }

    /**
     * The {@link ItemStack} tooltip.
     */
    public List<ITextComponent> getToolTip()
    {
        return toolTip;
    }

    /**
     * This event is fired with a null player during startup when populating search trees for tooltips.
     */
    @Override
    @Nullable
    public PlayerEntity getPlayer()
    {
        return super.getPlayer();
    }
}
