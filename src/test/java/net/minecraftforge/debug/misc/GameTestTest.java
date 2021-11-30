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

package net.minecraftforge.debug.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestRegistry;
import net.minecraft.gametest.framework.TestFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.List;

@Mod(GameTestTest.MODID)
public class GameTestTest
{
    public static final String MODID = "gametest_test";
    public static final boolean ENABLE = true;

    public GameTestTest()
    {
        if (ENABLE)
        {
            FMLJavaModLoadingContext.get().getModEventBus().register(this);
        }
    }

    @SubscribeEvent
    public void onRegisterGameTests(RegisterGameTestsEvent event)
    {
        // Registers all methods in this class annotated with @GameTest or @GameTestGenerator to the GameTestRegistry
        event.register(this.getClass());
    }

    /**
     * An example game test
     * <p><ul>
     * <li>Must take <b>one</b> parameter, the {@link GameTestHelper}</li>
     * <li>The return type is ignored, so it should be {@code void}</li>
     * <li>Can be {@code static} or non-static<p>
     * WARNING: If made non-static, then it will create an instance of the class every time it is run.</li>
     * </ul>
     * The default template name converts the containing class's name to all lowercase, and the method name to all lowercase.
     * In this example, the structure name would be "gametesttest.testwood" under the "gametest_test" namespace.
     */
    @GameTest(templateNamespace = MODID)
    public static void testWood(GameTestHelper helper)
    {
        // The woodPos is in the bottom center of the 3x3x3 structure
        BlockPos woodPos = new BlockPos(1, 1, 1);

        // assertBlockState will convert the relative woodPos into a real world block position and check it with the predicate.
        // Relative positions are made absolute by adding their value to the block position of the structure tile entity,
        // which is always the lowest northwest corner inside the structure.
        // If the predicate fails, the String supplier will be used to construct an exception message, which is thrown
        helper.assertBlockState(woodPos, b -> b.is(Blocks.OAK_LOG), () -> "Block was not an oak log");

        // If we got to this point, the assert succeeded, so the test has succeeded
        helper.succeed();
    }

    /**
     * An example game test generator.
     * <p>
     * A <b>game test generator</b> generates a collection of test functions.
     * It is called immediately when registered to GameTestRegistry.
     * <p><ul>
     * <li>Must return {@code Collection<TestFunction>} (or a subclass)</li>
     * <li>Must take no parameters</li>
     * <li>Can be {@code static} or non-static</li>
     * <p>
     * WARNING: If made non-static, then it will create an instance of the class every time it is run.</li>
     * </ul>
     */
    @GameTestGenerator
    public static List<TestFunction> generateTests()
    {
        // An example test function, run in the default batch, with the test name "teststone", and the structure name "gametesttest.teststone" under the "gametest_test" namespace.
        // No rotation, 100 ticks until the test times out if it does not fail or succeed, 0 ticks for setup time, and the actual code to run.
        TestFunction testStone = new TestFunction("defaultBatch", "teststone", new ResourceLocation(MODID, "gametesttest.teststone").toString(), Rotation.NONE, 100, 0, true,
                helper -> {
                    BlockPos stonePos = new BlockPos(1, 1, 1);

                    // This should always assert to true, since we set it then directly check it
                    helper.setBlock(stonePos, Blocks.STONE);
                    helper.assertBlockState(stonePos, b -> b.is(Blocks.STONE), () -> "Block was not stone");

                    helper.succeed();
                });
        return List.of(testStone);
    }
}
