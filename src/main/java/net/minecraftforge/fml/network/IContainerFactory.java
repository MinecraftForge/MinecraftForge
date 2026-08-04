/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fml.network;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;

public interface IContainerFactory<T extends Container> extends ContainerType.IFactory<T>
{
    T create(int windowId, PlayerInventory inv, PacketBuffer data);
    
    @Override
    default T create(int p_create_1_, PlayerInventory p_create_2_)
    {
        return create(p_create_1_, p_create_2_, null);
    }
}
