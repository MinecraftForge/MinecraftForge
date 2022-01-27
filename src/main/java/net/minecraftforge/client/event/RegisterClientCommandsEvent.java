/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.client.event;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraftforge.eventbus.api.Event;

/**
 * Register commands to be executed on the client using this event.
 * 
 * Some arguments behave differently:
 * <ul>
 * <li>{@link ResourceLocationArgument#getAdvancement(com.mojang.brigadier.context.CommandContext, String)} only returns advancements that are shown on the advancements screen
 * <li>{@link ObjectiveArgument#getObjective(com.mojang.brigadier.context.CommandContext, String)} only returns objectives that are set to display
 * </ul>
 */
public class RegisterClientCommandsEvent extends Event
{

    private final CommandDispatcher<CommandSourceStack> dispatcher;

    public RegisterClientCommandsEvent(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        this.dispatcher = dispatcher;
    }

    /**
     * @return The command dispatcher for registering commands to be executed on the client
     */
    public CommandDispatcher<CommandSourceStack> getDispatcher()
    {
        return dispatcher;
    }
}
