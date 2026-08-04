/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.chat;

import net.minecraft.command.CommandException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

//@Mod("client_command_test")
public class ClientCommandTest
{
    /*
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        ClientCommandHandler.instance.registerCommand(new TestCommand());
    }

    private class TestCommand extends CommandBase
    {
        @Override
        public String getName()
        {
            return "clientCommandTest";
        }

        @Override
        public String getUsage(ICommandSender sender)
        {
            return "clientCommandTest <block>";
        }

        @Override
        public boolean checkPermission(MinecraftServer server, ICommandSender sender)
        {
            return true;
        }

        @Override
        public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos)
        {
            if (args.length > 0)
            {
                return getListOfStringsMatchingLastWord(args, ForgeRegistries.BLOCKS.getKeys());
            }

            return Collections.emptyList();
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
        {
            if (args.length > 0)
            {
                sender.sendMessage(new StringTextComponent("Input: " + Arrays.toString(args)));
            }
            else
            {
                sender.sendMessage(new StringTextComponent("No arguments."));
            }
        }
    }
    */
}
