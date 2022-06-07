/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.event.entity.living;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import org.jetbrains.annotations.Nullable;

/**
 * BabyEntitySpawnEvent is fired just before a baby entity is about to be spawned. <br>
 * Parents will have disengaged their relationship. {@link Cancelable} <br>
 * It is possible to change the child completely by using {@link #setChild(AgeableMob)} <br>
 * This event is fired from {@link Animal#spawnChildFromBreeding(ServerLevel, Animal)} and
 * {@link Fox#spawnChildFromBreeding(ServerLevel, Animal)} <br>
 * <br>
 * {@link #parentA} contains the initiating parent entity.<br>
 * {@link #parentB} contains the secondary parent entity.<br>
 * {@link #causedByPlayer} contains the player responsible for the breading (if applicable).<br>
 * {@link #child} contains the child that will be spawned.<br>
 * <br>
 * This event is {@link Cancelable}.<br>
 * If this event is canceled, the child Entity is not added to the world, and the parents <br>
 * will no longer attempt to mate.
 * <br>
 * This event does not have a result. {@link HasResult}<br>
 * <br>
 * This event is fired on the {@link MinecraftForge#EVENT_BUS}.
 **/
@Cancelable
public class BabyEntitySpawnEvent extends net.minecraftforge.eventbus.api.Event
{
    private final Mob parentA;
    private final Mob parentB;
    private final Player causedByPlayer;
    private AgeableMob child;

    public BabyEntitySpawnEvent(Mob parentA, Mob parentB, @Nullable AgeableMob proposedChild)
    {
        //causedByPlayer calculated here to simplify the patch.
        Player causedByPlayer = null;
        if (parentA instanceof Animal) {
            causedByPlayer = ((Animal)parentA).getLoveCause();
        }

        if (causedByPlayer == null && parentB instanceof Animal)
        {
            causedByPlayer = ((Animal)parentB).getLoveCause();
        }

        this.parentA = parentA;
        this.parentB = parentB;
        this.causedByPlayer = causedByPlayer;
        this.child = proposedChild;
    }

    public Mob getParentA()
    {
        return parentA;
    }

    public Mob getParentB()
    {
        return parentB;
    }

    @Nullable
    public Player getCausedByPlayer()
    {
        return causedByPlayer;
    }

    @Nullable
    public AgeableMob getChild()
    {
        return child;
    }

    public void setChild(AgeableMob proposedChild)
    {
        child = proposedChild;
    }
}
