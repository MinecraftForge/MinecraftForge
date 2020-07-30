package net.minecraftforge.debug.misc;

import net.minecraft.world.GameRules;
import net.minecraftforge.fml.common.Mod;

@Mod("gamerulestest")
public class GameRulesTest {

    public static GameRules.Category TEST_CATEGORY = GameRules.Category.create("TEST_CATEGORY", "gamerule.category.test_category");

    public static final GameRules.RuleKey<GameRules.BooleanValue> TEST_BOOLEAN_GAMERULE = GameRules.func_234903_a_("test_boolean_gamerule", TEST_CATEGORY, GameRules.BooleanValue.create(true));
    public static final GameRules.RuleKey<GameRules.IntegerValue> TEST_INTEGER_GAMERULE = GameRules.func_234903_a_("test_integer_gamerule", TEST_CATEGORY, GameRules.IntegerValue.create(60));

}
