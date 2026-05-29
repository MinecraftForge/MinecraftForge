/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.server.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftsingularity.fml.ModList;

import java.util.Locale;
import java.util.stream.Collectors;

class ModListCommand {
    static ArgumentBuilder<CommandSourceStack, ?> register()
    {
        return Commands.literal("mods")
                .requires(Commands.hasPermission(Commands.LEVEL_ALL))
                .executes(ctx -> {
                            ctx.getSource().sendSuccess(() -> Component.translatable("commands.singularity.mods.list",
                                    ModList.applyForEachModFile(modFile ->
                                            // locator - filename : firstmod (version) - numberofmods\n
                                            String.format(Locale.ROOT, "%s %s : %s (%s) - %d",
                                                    modFile.getProvider().name().replace(' ', '_'),
                                                    modFile.getFileName(),
                                                    modFile.getModInfos().getFirst().getModId(),
                                                    modFile.getModInfos().getFirst().getVersion(),
                                                    modFile.getModInfos().size())).
                                        collect(Collectors.joining("\n\u2022 ","\n\u2022 ", ""))),
                                    false);
                            return 0;
                        }
                );
    }

}
