/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.entity.player.PlayerInventory;

public class PlayerInvWrapper extends CombinedInvWrapper
{
    public PlayerInvWrapper(PlayerInventory inv)
    {
        super(new PlayerMainInvWrapper(inv), new PlayerArmorInvWrapper(inv), new PlayerOffhandInvWrapper(inv));
    }
}
