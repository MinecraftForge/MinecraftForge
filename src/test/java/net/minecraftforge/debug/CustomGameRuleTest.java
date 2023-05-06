/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.world.level.GameRules;
import net.minecraftforge.fml.common.Mod;

/**
 * Simple test to ensure custom game rules can be registered correctly and used in game.
 * <p>
 * To test these game rules use the following commands.
 * <br>If the game rules are registered correctly, they should show up as auto-completion values and be able to be changed to valid values based on their types.
 * <br>These game rules should also show up and be editable under the {@code Edit Game Rules} screen, when creating a new world.
 * <br>{@code Create new world > More (tab) > Game Rules > Misc}
 * <ul>
 *     <li><pre>{@code /gamerule custom_game_rule_test:custom_boolean_game_rule <true|false>}</pre></li>
 *     Should be able to be set to either {@code true} or {@code false} (Defaulting to {@code true}).
 *
 *     <li><pre>{@code /gamerule custom_game_rule_test:custom_integer_game_rule <some integer>}</pre></li>
 *     Should be able to be set to any integer value (Defaulting to {@code 1337}).
 * </ul>
 */
@Mod(CustomGameRuleTest.TEST_ID)
public class CustomGameRuleTest
{
    public static final String TEST_ID = "custom_game_rule_test";
    public static final GameRules.Key<GameRules.BooleanValue> CUSTOM_BOOLEAN_GAME_RULE = GameRules.register("%s:custom_boolean_game_rule".formatted(TEST_ID), GameRules.Category.MISC, GameRules.BooleanValue.create(true));
    public static final GameRules.Key<GameRules.IntegerValue> CUSTOM_INTEGER_GAME_RULE = GameRules.register("%s:custom_integer_game_rule".formatted(TEST_ID), GameRules.Category.MISC, GameRules.IntegerValue.create(1337));
}
