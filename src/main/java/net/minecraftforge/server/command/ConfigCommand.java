/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraftsingularity.fml.config.ConfigTracker;
import net.minecraftsingularity.fml.config.ModConfig;

import java.io.File;

public class ConfigCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("config").
                        then(ShowFile.register())
        );
    }

    public static class ShowFile {
        static ArgumentBuilder<CommandSourceStack, ?> register() {
            return Commands.literal("showfile").
                    requires(Commands.hasPermission(Commands.LEVEL_ALL)).
                    then(Commands.argument("mod", ModIdArgument.modIdArgument()).
                        then(Commands.argument("type", EnumArgument.enumArgument(ModConfig.Type.class)).
                            executes(ShowFile::showFile)
                        )
                    );
        }

        private static int showFile(final CommandContext<CommandSourceStack> context) {
            final String modId = context.getArgument("mod", String.class);
            final ModConfig.Type type = context.getArgument("type", ModConfig.Type.class);
            final String configFileName = ConfigTracker.getConfigFileName(modId, type);
            if (configFileName != null) {
                File f = new File(configFileName);
                context.getSource().sendSuccess(() -> Component.translatable("commands.config.getwithtype",
                        modId, type,
                        Component.literal(f.getName()).withStyle(ChatFormatting.UNDERLINE).
                                withStyle((style) -> style.withClickEvent(new ClickEvent.OpenFile(f.getAbsolutePath())))
                ), true);
            } else {
                context.getSource().sendSuccess(() -> Component.translatable("commands.config.noconfig", modId, type),
                        true);
            }
            return 0;
        }
    }
}
