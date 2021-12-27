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

package net.minecraftforge.debug.chat;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
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

    private void init(RegisterClientCommandsEvent event)
    {
        event.getDispatcher().register(
                Commands.literal("clientcommandtest")
                        // Used for checking suggestion providers that aren't registered
                        .then(Commands.literal("rawsuggest")
                                .then(Commands.argument("block", ResourceLocationArgument.id())
                                        .suggests((c, b) -> SharedSuggestionProvider.suggestResource(ForgeRegistries.BLOCKS.getKeys(), b))
                                        .executes(this::testCommand)))
                        // Used for checking suggestion providers that are registered
                        .then(Commands.literal("registeredsuggest").then(
                                Commands.argument("block", ResourceLocationArgument.id())
                                        .suggests(SuggestionProviders.AVAILABLE_BIOMES)
                                        .executes(this::testCommand)))
                        // Used for checking if attempting to get the server on the client side errors
                        .then(Commands.literal("server")
                                .executes((context) -> {
                                    context.getSource().getServer();
                                    context.getSource().sendSuccess(new TextComponent("Successfully called getServer should have errored"), false);
                                    return 1;
                                }))
                        // Used for checking if attempting to get the server level on the client side errors
                        .then(Commands.literal("level")
                                .executes((context) -> {
                                    context.getSource().getLevel();
                                    context.getSource().sendSuccess(new TextComponent("Successfully called getLevel should have errored"), false);
                                    return 1;
                                }))
                        // Used for checking if getting a known objective argument works on the client side
                        .then(Commands.literal("get_objective")
                                .then(Commands.argument("objective", ObjectiveArgument.objective())
                                        .executes((context) -> {
                                            context.getSource().sendSuccess(new TextComponent("Regular: ")
                                                    .append(ObjectiveArgument.getObjective(context, "objective").getFormattedDisplayName()), false);
                                            return 1;
                                        })))
                        // Used for checking if getting a known advancement works on the client side
                        .then(Commands.literal("get_advancement")
                                .then(Commands.argument("advancement", ResourceLocationArgument.id())
                                        .executes((context) -> {
                                            context.getSource().sendSuccess(ResourceLocationArgument.getAdvancement(context, "advancement").getChatComponent(),
                                                    false);
                                            return 1;
                                        })))
                        // Used for checking if getting a known recipe works on the client side
                        .then(Commands.literal("get_recipe")
                                .then(Commands.argument("recipe", ResourceLocationArgument.id())
                                        .executes((context) -> {
                                            context.getSource()
                                                    .sendSuccess(ResourceLocationArgument.getRecipe(context, "recipe").getResultItem().getDisplayName(), false);
                                            return 1;
                                        })))
                        // Used for checking if getting a team works on the client side
                        .then(Commands.literal("get_team")
                                .then(Commands.argument("team", TeamArgument.team())
                                        .executes((context) -> {
                                            context.getSource().sendSuccess(TeamArgument.getTeam(context, "team").getFormattedDisplayName(), false);
                                            return 1;
                                        })))
                        // Used for checking if a block position is valid works on the client side
                        .then(Commands.literal("get_loaded_blockpos")
                                .then(Commands.argument("blockpos", BlockPosArgument.blockPos())
                                        .executes((context) -> {
                                            context.getSource()
                                                    .sendSuccess(new TextComponent(BlockPosArgument.getLoadedBlockPos(context, "blockpos").toString()), false);
                                            return 1;
                                        }))));
    }

    private int testCommand(CommandContext<CommandSourceStack> context)
    {
        context.getSource().sendSuccess(new TextComponent("Input: " + ResourceLocationArgument.getId(context, "block")), false);
        context.getSource().sendSuccess(new TextComponent("Teams: " + context.getSource().getAllTeams()), false);
        context.getSource().sendSuccess(new TextComponent("Players: " + context.getSource().getOnlinePlayerNames()), false);
        context.getSource().sendSuccess(new TextComponent("First recipe: " + context.getSource().getRecipeNames().findFirst().get()), false);
        context.getSource().sendSuccess(new TextComponent("Levels: " + context.getSource().levels()), false);
        context.getSource().sendSuccess(new TextComponent("Registry Access: " + context.getSource().registryAccess()), false);
        return 0;
    }
}
