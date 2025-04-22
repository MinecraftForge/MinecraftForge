package net.minecraftforge.debug.chunk;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
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

    private static final int MAX_CHUNK_LOCATION_ATTEMPTS = 5;

    public LightingEventTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest
    public static void testLightingEventFires(GameTestHelper helper) {
        var eventFired = helper.boolFlag("eventFired");
        helper.<ChunkEvent.LightingCalculated>addEventListener(event -> eventFired.set(true));

        var random = RandomSource.create();
        var level = helper.getLevel();
        var chunkSource = level.getChunkSource();

        // try to find a far away chunk that is not already ticking
        int attempts;
        var origin = helper.absolutePos(BlockPos.ZERO);
        ChunkAccess chunk = null;
        for (attempts = 0; attempts < MAX_CHUNK_LOCATION_ATTEMPTS && chunk == null; attempts++) {
            int x = random.nextInt(1, 10) * 10000;
            int z = random.nextInt(1, 10) * 10000;
            chunk = level.getChunk(origin.offset(x, 0, z));

            // If the chunk is already ticking, we can't force it
            chunk = chunkSource.isPositionTicking(chunk.getPos().toLong()) ? null : chunk;
        }

        if (chunk == null) {
            helper.fail(Component.literal("Failed to find a far away chunk that is not already ticking after " + MAX_CHUNK_LOCATION_ATTEMPTS + " attempts"));
            return;
        } else if (attempts > 1) {
            helper.say("WARNING: Finding a far away chunk took " + attempts + " attempts.", ChatFormatting.YELLOW);
        }

        helper.runAfterDelay(20, () -> {
            if (!eventFired.getBool()) {
                helper.fail("LightingCalculated event was not fired!");
            }

            helper.succeed();
        });
    }
}
