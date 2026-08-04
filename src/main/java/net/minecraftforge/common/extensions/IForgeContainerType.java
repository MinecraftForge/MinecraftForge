/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.extensions;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

public interface IForgeContainerType<T>
{
    static <T extends Container> ContainerType<T> create(net.minecraftforge.fml.network.IContainerFactory<T> factory)
    {
        return new ContainerType<>(factory);
    }
    
    T create(int windowId, PlayerInventory playerInv, PacketBuffer extraData);
}
