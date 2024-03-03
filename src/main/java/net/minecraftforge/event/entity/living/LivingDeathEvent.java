/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/**
 * LivingDeathEvent is fired when an Entity dies. <br>
 * This event is fired whenever an Entity dies in
 * {@link LivingEntity#die(DamageSource)},
 * {@link Player#die(DamageSource)}, and
 * {@link ServerPlayer#die(DamageSource)}. <br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingDeath(LivingEntity, DamageSource)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the entity to die. <br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not die.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class LivingDeathEvent extends LivingEvent
{
    private final DamageSource source;
    public LivingDeathEvent(LivingEntity entity, DamageSource source)
    {
        super(entity);
        this.source = source;
    }

    public DamageSource getSource()
    {
        return source;
    }
}
