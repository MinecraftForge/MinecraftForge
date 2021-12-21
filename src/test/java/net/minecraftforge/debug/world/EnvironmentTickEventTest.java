package net.minecraftforge.debug.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(EnvironmentTickEventTest.MODID)
public class EnvironmentTickEventTest
{
    public static final String MODID = "environment_tick_event_test";
    public static final boolean ENABLED = true;

    public EnvironmentTickEventTest()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onEnvironmentTick(final TickEvent.EnvironmentTickEvent event)
    {
        if (ENABLED && event.phase == TickEvent.Phase.END) // recreate snowing logic
        {
            Level level = event.level;
            if (level.isRaining() && level.random.nextInt(16) == 0)
            {
                ChunkPos chunkPos = event.chunk.getPos();
                BlockPos pos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, level.getBlockRandomPos(chunkPos.getMinBlockX(), 0, chunkPos.getMinBlockZ(), 15));
                BlockPos belowPos = pos.below();
                Biome biome = level.getBiome(pos);
                if (biome.shouldSnow(level, pos)) // snow obsidian
                {
                    level.setBlockAndUpdate(belowPos, Blocks.CRYING_OBSIDIAN.defaultBlockState());
                }
            }
        }
    }
}
