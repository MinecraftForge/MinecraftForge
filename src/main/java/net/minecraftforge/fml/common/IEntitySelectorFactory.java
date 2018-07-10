/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.common;

import com.google.common.base.Predicate;
import net.minecraft.command.EntitySelector;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Allows mods to create custom selectors in commands.
 * Registered in {@link net.minecraftforge.fml.common.registry.GameRegistry#registerEntitySelector(IEntitySelectorFactory, String...)}
 * For an example implementation, see CustomEntitySelectorTest
 */
public interface IEntitySelectorFactory
{
    /**
     * Called every time a command that contains entity selectors is executed
     *
     * @param arguments    A map with all arguments and their values
     * @param mainSelector The main selector string (e.g. 'a' for all players or 'e' for all entities)
     * @param sender       The sender of the command
     * @param position     A position either specified in the selector arguments or by the players position. See {@link EntitySelector#getPosFromArguments(Map, Vec3d)}
     * @return A list of new predicates, can be empty ({@link Collections#emptyList()} but not null.
     */
    @Nonnull List<Predicate<Entity>> createPredicates(Map<String, String> arguments, String mainSelector, ICommandSender sender, Vec3d position);
}
