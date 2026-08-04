/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;

public class PlayerContainerEvent extends PlayerEvent
{
    private final Container container;
    public PlayerContainerEvent(PlayerEntity player, Container container)
    {
        super(player);
        this.container = container;
    }

    public static class Open extends PlayerContainerEvent
    {
        public Open(PlayerEntity player, Container container)
        {
            super(player, container);
        }
    }
    public static class Close extends PlayerContainerEvent
    {
        public Close(PlayerEntity player, Container container)
        {
            super(player, container);
        }
    }

    public Container getContainer()
    {
        return container;
    }
}
