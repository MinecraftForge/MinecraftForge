package net.minecraftforge.debug.world;

import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.gen.settings.StructureSpreadSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkGeneratorLoadEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ChunkGeneratorLoadEventTest.MODID)
public class ChunkGeneratorLoadEventTest
{
    public static final String MODID = "chunk_generator_load_event_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final boolean ENABLED = false;

    public ChunkGeneratorLoadEventTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onChunkGeneratorLoading);
        }
    }

    public void onChunkGeneratorLoading(ChunkGeneratorLoadEvent event)
    {
        LOGGER.info("Seed: {}", event.getSeed());
        if (event.getStructureSeparationSettings().containsKey(Structure.field_236370_f_))
        {
            event.getStructureSeparationSettings().put(Structure.field_236370_f_, new StructureSeparationSettings(3, 1, 12345));
        }
        if (event.getStructureSeparationSettings().containsKey(Structure.field_236369_e_))
        {
            event.getStructureSeparationSettings().put(Structure.field_236369_e_, new StructureSeparationSettings(3, 0, 54321));
        }
        event.setStructureSpreadSettings(new StructureSpreadSettings(5, 5, 256)); // This makes strongholds pretty common.
    }

}
