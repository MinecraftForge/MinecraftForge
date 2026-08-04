/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import java.util.ArrayList;
import java.util.Collection;

import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.LivingEntity;

/**
 * LivingDropsEvent is fired when an Entity's death causes dropped items to appear.<br>
 * This event is fired whenever an Entity dies and drops items in
 * {@link EntityLivingBase#onDeath(DamageSource)}.<br>
 * <br>
 * This event is fired via the {@link ForgeHooks#onLivingDrops(EntityLivingBase, DamageSource, ArrayList, int, boolean)}.<br>
 * <br>
 * {@link #source} contains the DamageSource that caused the drop to occur.<br>
 * {@link #drops} contains the ArrayList of EntityItems that will be dropped.<br>
 * {@link #lootingLevel} contains the amount of loot that will be dropped.<br>
 * {@link #recentlyHit} determines whether the Entity doing the drop has recently been damaged.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity does not drop anything.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@net.minecraftforge.eventbus.api.Cancelable
public class LivingDropsEvent extends LivingEvent
{
    private final DamageSource source;
    private final Collection<ItemEntity> drops;
    private final int lootingLevel;
    private final boolean recentlyHit;

    public LivingDropsEvent(LivingEntity entity, DamageSource source, Collection<ItemEntity> drops, int lootingLevel, boolean recentlyHit)
    {
        super(entity);
        this.source = source;
        this.drops = drops;
        this.lootingLevel = lootingLevel;
        this.recentlyHit = recentlyHit;
    }

    public DamageSource getSource()
    {
        return source;
    }

    public Collection<ItemEntity> getDrops()
    {
        return drops;
    }

    public int getLootingLevel()
    {
        return lootingLevel;
    }

    public boolean isRecentlyHit()
    {
        return recentlyHit;
    }
}
