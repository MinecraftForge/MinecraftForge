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
