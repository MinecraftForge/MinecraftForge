package net.minecraftforge.debug.world;

import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.StructurePoolCreatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(StructurePoolCreatedEventTest.MODID)
public class StructurePoolCreatedEventTest {

    public static final String MODID = "structure_pool_created_event_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);

    public StructurePoolCreatedEventTest() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH, this::onStructurePoolCreated);
    }

    private void onStructurePoolCreated(StructurePoolCreatedEvent event) {
        event.addTemplate(StructurePoolElement.legacy(MODID + ":" + "test"), 8);
        LOGGER.info("Added custom template to pool: " + event.getCreatedPoolName());
    }

}
