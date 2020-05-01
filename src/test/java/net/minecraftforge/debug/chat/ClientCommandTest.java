/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

package net.minecraftforge.debug.chat;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.CommandRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod("client_command_test")
public class ClientCommandTest
{
    public ClientCommandTest()
    {
        MinecraftForge.EVENT_BUS.addListener(this::init);
    }

    private void init(CommandRegistryEvent event)
    {
        LiteralArgumentBuilder<ISuggestionProvider> cmd = LiteralArgumentBuilder.literal("clientcommandtest");
        RequiredArgumentBuilder<ISuggestionProvider, ResourceLocation> arg = RequiredArgumentBuilder.argument("block", ResourceLocationArgument.resourceLocation());
        SuggestionProvider<ISuggestionProvider> suggestions = (c,b)->ISuggestionProvider.suggestIterable(ForgeRegistries.BLOCKS.getKeys(), b);
        event.getDispatcher().register(cmd.requires(c->true).then(arg.suggests(suggestions).executes(new TestCommand())));
    }

    private static class TestCommand implements Command<ISuggestionProvider>
    {
        @Override
        public int run(CommandContext<ISuggestionProvider> commandContext)
        {
            Minecraft.getInstance().player.sendMessage(new StringTextComponent("Input: " + commandContext.getArgument("block", ResourceLocation.class).toString()));
            return 0;
        }
    }
}
