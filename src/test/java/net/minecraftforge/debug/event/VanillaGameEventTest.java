package net.minecraftforge.debug.event;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.event.VanillaGameEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.test.BaseTestMod;

@GameTestHolder("forge." + VanillaGameEventTest.MODID)
@Mod(VanillaGameEventTest.MODID)
public class VanillaGameEventTest extends BaseTestMod {
    static final String MODID = "vanilla_game_event";

    public VanillaGameEventTest(FMLJavaModLoadingContext context) {
        super(context);
    }

    @GameTest(template = "forge:empty3x3x3")
    public static void game_event_matches(GameTestHelper helper) {
        // Place a button in the test area
        var pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, Blocks.STONE_BUTTON);
        
        // Press the button, which is supposed to trigger the BLOCK_ACTIVATE game event
        helper.pressButton(pos);
        
        // Confirm that the button has been replaced by a diamond block
        helper.assertBlockPresent(Blocks.DIAMOND_BLOCK, pos);
        helper.succeed();
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Listener {
        @SubscribeEvent
        public static void vanillaEvent(VanillaGameEvent event) {
            // Replace any activated block with a diamond block
            if (GameEvent.BLOCK_ACTIVATE.is(event.getVanillaEventHolder())) {
                event.getLevel().setBlock(BlockPos.containing(event.getEventPosition()), Blocks.DIAMOND_BLOCK.defaultBlockState(), 3);
            }
        }
    }
}
