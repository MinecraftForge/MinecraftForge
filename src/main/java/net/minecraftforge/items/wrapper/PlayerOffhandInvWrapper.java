/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.items.wrapper;

import net.minecraft.world.entity.player.Inventory;

public class PlayerOffhandInvWrapper extends RangedWrapper
{
    public PlayerOffhandInvWrapper(Inventory inv)
    {
        super(new InvWrapper(inv), inv.items.size() + inv.armor.size(),
                inv.items.size() + inv.armor.size() + inv.offhand.size());
    }
}
