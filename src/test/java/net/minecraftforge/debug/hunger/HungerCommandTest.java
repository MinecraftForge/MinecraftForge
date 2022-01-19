/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.hunger;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.command.EnumArgument;

/**
 * NOTE: This does NOT test a feature in Forge itself. This instead provides a utility to those who are testing
 * other test mods within this package, and want to quickly change various FoodData levels on their player.
 * It is recommended to enable this mod when testing other mods within this package.
 *
 * This command adds:
 * - /hunger list: Lists Hunger/Saturation/Exhaustion levels to chat
 * - /hunger add [nutrition/saturation/exhaustion] [delta]: Adds (or subtracts) a given amount from the chosen stat
 * - /hunger set [nutrition/saturation/exhaustion] [value]: Sets the given stat to the provided value
 * - /hunger heal: Sets nutrition and saturation values to their max and resets exhaustion to 0
 * - /hunger starve: Sets nutrition, saturation, and exhaustion to 0
 */
@Mod("hunger_command_test")
@Mod.EventBusSubscriber
public class HungerCommandTest
{
    private static final boolean ENABLED = false;

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event)
    {
        if (ENABLED)
        {
            event.getDispatcher().register(
                Commands.literal("hunger")
                    .then(hungerListCommand())
                    .then(hungerAddCommand())
                    .then(hungerSetCommand())
                    .then(hungerHealCommand())
                    .then(hungerStarveCommand())
            );
        }
    }

    private enum HungerStatType
    {
        NUTRITION,
        SATURATION,
        EXHAUSTION
    }

    private static ArgumentBuilder<CommandSourceStack, ?> hungerListCommand()
    {
        return Commands.literal("list")
            .requires(cs -> cs.hasPermission(0))
            .executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayerOrException();
                int maxHunger = ForgeEventFactory.getMaxHunger(player);
                ctx.getSource().sendSuccess(new TextComponent("Hunger: ")
                    .append(Integer.toString(player.getFoodData().getFoodLevel()))
                    .append("/")
                    .append(Integer.toString(maxHunger)), false);
                ctx.getSource().sendSuccess(new TextComponent("Saturation: ")
                    .append(Float.toString(player.getFoodData().getSaturationLevel()))
                    .append("/")
                    .append(Float.toString(player.getFoodData().getFoodLevel()))
                    .append(" (")
                    .append(Float.toString(maxHunger))
                    .append(")"), false);
                ctx.getSource().sendSuccess(new TextComponent("Exhaustion: ")
                    .append(Float.toString(player.getFoodData().getExhaustionLevel()))
                    .append("/")
                    .append(Float.toString(ForgeEventFactory.getMaxExhaustion(player)))
                    .append(" (")
                    .append(Float.toString(ForgeEventFactory.getExhaustionCap(player)))
                    .append(")"), false);
                return 3;
            });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> hungerAddCommand()
    {
        return Commands.literal("add")
            .requires(cs -> cs.hasPermission(0))
            .then(Commands.argument("stat", EnumArgument.enumArgument(HungerStatType.class))
                .then(Commands.argument("delta", FloatArgumentType.floatArg())
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        Float delta = ctx.getArgument("delta", Float.class);
                        switch (ctx.getArgument("stat", HungerStatType.class))
                        {
                            case NUTRITION -> {
                                int foodDelta = Mth.clamp(delta.intValue(), -player.getFoodData().getFoodLevel(), ForgeEventFactory.getMaxHunger(player) - player.getFoodData().getFoodLevel());
                                player.getFoodData().addFood(foodDelta);
                            }
                            case SATURATION -> {
                                float satDelta = Mth.clamp(delta, -player.getFoodData().getSaturationLevel(), player.getFoodData().getFoodLevel() - player.getFoodData().getSaturationLevel());
                                player.getFoodData().addSaturation(satDelta);
                            }
                            case EXHAUSTION -> {
                                float exhaustDelta = Mth.clamp(delta, -player.getFoodData().getExhaustionLevel(), Float.MAX_VALUE); // Upper bound taken care of by below call
                                player.getFoodData().addExhaustion(exhaustDelta);
                            }
                        }
                        return 0;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> hungerSetCommand()
    {
        return Commands.literal("set")
            .requires(cs -> cs.hasPermission(0))
            .then(Commands.argument("stat", EnumArgument.enumArgument(HungerStatType.class))
                .then(Commands.argument("value", FloatArgumentType.floatArg(0.0F))
                    .executes(ctx -> {
                        ServerPlayer player = ctx.getSource().getPlayerOrException();
                        Float value = ctx.getArgument("value", Float.class);
                        switch (ctx.getArgument("stat", HungerStatType.class))
                        {
                            case NUTRITION -> player.getFoodData().setFoodLevel(Math.min(value.intValue(), ForgeEventFactory.getMaxHunger(player)));
                            case SATURATION -> player.getFoodData().setSaturation(Math.min(value, player.getFoodData().getFoodLevel()));
                            case EXHAUSTION -> player.getFoodData().setExhaustion(Math.min(value, ForgeEventFactory.getMaxExhaustion(player)));
                        }
                        return 0;
                    })
                )
            );
    }

    private static ArgumentBuilder<CommandSourceStack, ?> hungerHealCommand()
    {
        return Commands.literal("heal")
            .requires(cs -> cs.hasPermission(0))
            .executes(ctx -> {
                ServerPlayer player = ctx.getSource().getPlayerOrException();
                player.getFoodData().setFoodLevel(ForgeEventFactory.getMaxHunger(player));
                player.getFoodData().setSaturation(player.getFoodData().getFoodLevel());
                player.getFoodData().setExhaustion(0.0F);
                return 0;
            });
    }

    private static ArgumentBuilder<CommandSourceStack, ?> hungerStarveCommand()
    {
        return Commands.literal("starve")
            .requires(cs -> cs.hasPermission(0))
            .executes(ctx -> {
                FoodData foodData = ctx.getSource().getPlayerOrException().getFoodData();
                foodData.setFoodLevel(0);
                foodData.setSaturation(0.0F);
                foodData.setExhaustion(0.0F);
                return 0;
            });
    }
}
