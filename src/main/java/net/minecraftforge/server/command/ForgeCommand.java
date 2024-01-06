/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public class ForgeCommand
{
    public ForgeCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            LiteralArgumentBuilder.<CommandSourceStack>literal("forge")
            .then(CommandTps.register())
            .then(CommandTrack.register())
            .then(CommandEntity.register())
            .then(CommandGenerate.register())
            .then(CommandDimensions.register())
            .then(CommandSetDimension.register())
            .then(CommandModList.register())
        );
    }
}
