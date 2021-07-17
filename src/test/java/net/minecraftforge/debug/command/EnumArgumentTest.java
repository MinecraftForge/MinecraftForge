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

package net.minecraftforge.debug.command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.EnumArgument;

import com.mojang.brigadier.arguments.StringArgumentType;

@Mod("enum_argument_test")
public class EnumArgumentTest
{
    public static final boolean ENABLE = true;

    public EnumArgumentTest()
    {
        if (ENABLE)
            MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("enumargumenttest")
                .then(Commands.argument("string", StringArgumentType.string())
                        .executes(context -> {
                            context.getSource().sendSuccess(new TextComponent("string: " + StringArgumentType.getString(context, "string")), false);

                            return 1;
                        }))
                .then(Commands.argument("enum", EnumArgument.enumArgument(ExampleEnum.class))
                        .executes(context -> {
                            context.getSource()
                                    .sendSuccess(new TextComponent("enum: " + context.getArgument("enum", ExampleEnum.class)), false);

                            return 1;
                        })));
    }

    public enum ExampleEnum
    {
        A, B
    }
}
