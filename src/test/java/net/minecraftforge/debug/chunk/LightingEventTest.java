package net.minecraftforge.debug.chunk;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * This is a test for the ChunkEvent.LightingCalculated event.
 */
@Mod(LightingEventTest.MODID)
@GameTestNamespace("forge")
public class LightingEventTest extends BaseTestMod {
    public static final String MODID = "lighting_event_test";

    // Atomic boolean is to ensure multi-thread safety
    private static final AtomicBoolean eventFired = new AtomicBoolean(false);

    public LightingEventTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public static void onLightingCalculated(ChunkEvent.LightingCalculated event) {
        ChunkAccess chunk = event.getChunk();
        System.out.println("[DEBUG] LightingCalculated event fired for chunk: " + chunk.getPos());
        eventFired.set(true);
    }

    @GameTest(structure = "lighting_event_test:lighting_event_test")
    public static void testLightingEventFires(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(BlockPos.ZERO);
        helper.getLevel().getChunkAt(pos); // Trigger chunk load (which eventually fires lighting logic)

        helper.runAfterDelay(60, () -> {
            if (eventFired.get()) {
                helper.succeed();
            } else {
                helper.fail("LightingCalculated event was not fired!");
            }
        });
    }
}
