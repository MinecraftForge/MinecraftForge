/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.command;

import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
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
                            context.getSource().sendSuccess(() -> Component.literal("string: " + StringArgumentType.getString(context, "string")), false);

                            return 1;
                        }))
                .then(Commands.argument("enum", EnumArgument.enumArgument(ExampleEnum.class))
                        .executes(context -> {
                            context.getSource()
                                    .sendSuccess(() -> Component.literal("enum: " + context.getArgument("enum", ExampleEnum.class)), false);

                            return 1;
                        })));
    }

    public enum ExampleEnum
    {
        A, B
    }
}
