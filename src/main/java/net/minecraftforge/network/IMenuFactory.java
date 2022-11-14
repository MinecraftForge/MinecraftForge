/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.network;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public interface IMenuFactory<T extends AbstractContainerMenu> extends MenuType.MenuSupplier<T>
{
    T create(int windowId, Inventory inv, @Nullable  FriendlyByteBuf data);

    @ApiStatus.Internal
    @Override
    default T create(int windowId, Inventory inv)
    {
        return create(windowId, inv, null);
    }
}
