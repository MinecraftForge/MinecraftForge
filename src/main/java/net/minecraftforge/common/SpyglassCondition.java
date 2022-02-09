/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.common;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.function.Predicate;

/**
 * A condition which will be evaluated on the client to determine whether the player should be considered to be 'scoping' with a spyglass.
 * Only one registered condition needs to be true for scoping to occur.
 * <strong>The condition will be tested very frequently (typically every frame).</strong>
 *
 * Must be registered in {@link ForgeRegistries#SPYGLASS_CONDITIONS} to take effect.
 */
public abstract class SpyglassCondition extends ForgeRegistryEntry<SpyglassCondition> implements Predicate<Player>
{

}
