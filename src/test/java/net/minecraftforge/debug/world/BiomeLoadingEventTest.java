package net.minecraftforge.debug.world;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BiomeLoadingEventTest.MODID)
public class BiomeLoadingEventTest {
    static final String MODID = "biome_loading_event_test";
    private static final Logger LOGGER = LogManager.getLogger(MODID);
    private static final boolean ENABLED = false;
    public BiomeLoadingEventTest(){
        if(ENABLED) {
            MinecraftForge.EVENT_BUS.addListener(this::onBiomeLoading);
        }
    }
    public void onBiomeLoading(BiomeLoadingEvent event){
        ResourceLocation biome = event.getName();
        LOGGER.info(String.format("Biome loaded: %s", biome.toString()));
    }
}
