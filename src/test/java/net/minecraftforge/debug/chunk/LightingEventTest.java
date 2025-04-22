package net.minecraftforge.debug.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

/**
 * This is a test for the ChunkEvent.LightingCalculated event.
 */
@GameTestNamespace("forge")
@Mod(LightingEventTest.MODID)
public class LightingEventTest extends BaseTestMod {
    public static final String MODID = "lighting_event_test";

    public LightingEventTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest
    public static void testLightingEventFires(GameTestHelper helper) {
        var eventFired = helper.boolFlag("eventFired");
        helper.<ChunkEvent.LightingCalculated>addEventListener(event -> eventFired.set(true));

        BlockPos pos = helper.absolutePos(BlockPos.ZERO);
        helper.getLevel().getChunkAt(pos); // Trigger chunk load (which eventually fires lighting logic)

        helper.runAfterDelay(20, () -> {
            if (!eventFired.getBool()) {
                helper.fail("LightingCalculated event was not fired!");
            }

            helper.succeed();
        });
    }
}
