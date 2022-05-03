/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when an {@link EntityAnimal} is tamed. <br>
 * It is fired via {@link ForgeEventFactory#onAnimalTame(EntityAnimal, EntityPlayer)}.
 * Forge fires this event for applicable vanilla animals, mods need to fire it themselves.
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}. If canceled, taming the animal will fail.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class AnimalTameEvent extends LivingEvent
{
    private final AnimalEntity animal;
    private final PlayerEntity tamer;

    public AnimalTameEvent(AnimalEntity animal, PlayerEntity tamer)
    {
        super(animal);
        this.animal = animal;
        this.tamer = tamer;
    }

    public AnimalEntity getAnimal()
    {
        return animal;
    }

    public PlayerEntity getTamer()
    {
        return tamer;
    }
}
