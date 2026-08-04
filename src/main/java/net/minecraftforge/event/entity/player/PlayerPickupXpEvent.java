/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.player;

import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * Use {@link PlayerXpEvent.PickupXp}
 */
@Cancelable
@Deprecated
public class PlayerPickupXpEvent extends PlayerXpEvent.PickupXp
{

    public PlayerPickupXpEvent(PlayerEntity player, ExperienceOrbEntity orb)
    {
        super(player, orb);
    }

}
