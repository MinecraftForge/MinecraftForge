/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.server.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public class singularityCommand
{
    public singularityCommand(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(
            LiteralArgumentBuilder.<CommandSourceStack>literal("singularity")
            .then(TPSCommand.register())
            .then(TrackCommand.register())
            .then(EntityCommand.register())
            .then(GenerateCommand.register())
            .then(DimensionsCommand.register())
            .then(ModListCommand.register())
            .then(TagsCommand.register())
        );
    }
}
