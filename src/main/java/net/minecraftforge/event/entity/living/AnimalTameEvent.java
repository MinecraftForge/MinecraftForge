/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * This event is fired when an {@link Animal} is tamed. <br>
 * It is fired via {@link ForgeEventFactory#onAnimalTame(Animal, Player)}.
 * Forge fires this event for applicable vanilla animals, mods need to fire it themselves.
 * This event is {@link net.minecraftforge.eventbus.api.Cancelable}. If canceled, taming the animal will fail.
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 */
@Cancelable
public class AnimalTameEvent extends LivingEvent
{
    private final Animal animal;
    private final Player tamer;

    public AnimalTameEvent(Animal animal, Player tamer)
    {
        super(animal);
        this.animal = animal;
        this.tamer = tamer;
    }

    public Animal getAnimal()
    {
        return animal;
    }

    public Player getTamer()
    {
        return tamer;
    }
}
