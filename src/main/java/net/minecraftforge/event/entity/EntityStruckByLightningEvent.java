/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;

/**
 * EntityStruckByLightningEvent is fired when an Entity is about to be struck by lightening.<br>
 * This event is fired whenever an EntityLightningBolt is updated to strike an Entity in
 * {@link EntityLightningBolt#onUpdate()} via {@link ForgeEventFactory#onEntityStruckByLightning(Entity, EntityLightningBolt)}.<br>
 * <br>
 * {@link #lightning} contains the instance of EntityLightningBolt attempting to strike an entity.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the Entity is not struck by the lightening.<br>
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.<br>
 **/
@net.minecraftforge.eventbus.api.Cancelable
public class EntityStruckByLightningEvent extends EntityEvent
{
    private final LightningBoltEntity lightning;

    public EntityStruckByLightningEvent(Entity entity, LightningBoltEntity lightning)
    {
        super(entity);
        this.lightning = lightning;
    }

    public LightningBoltEntity getLightning()
    {
        return lightning;
    }
}
