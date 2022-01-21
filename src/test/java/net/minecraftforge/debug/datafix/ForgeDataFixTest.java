package net.minecraftforge.debug.datafix;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.datafix.ConfigureDataFixSchemaEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("forge_datafix_test")
public class ForgeDataFixTest
{

    public static final  boolean ENABLE = true;
    private static final Logger  LOGGER = LogManager.getLogger();

    public ForgeDataFixTest()
    {
        if (ENABLE)
        {
            registerEventHandler();
        }
    }

    private void registerEventHandler() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(
          this::onDataFixSchemaTypeConfiguration
        );
    }

    private void onDataFixSchemaTypeConfiguration(ConfigureDataFixSchemaEvent configureDataFixSchemaEvent) {
        LOGGER.info("Initializing DFU schema: " + configureDataFixSchemaEvent.getSchema().getVersionKey());

        //Register something to all the schemas.
        configureDataFixSchemaEvent.registerSimpleBlockEntity(new ResourceLocation("forge_datafix_test:simple_block_entity"));
    }
}
