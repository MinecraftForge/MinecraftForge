package net.minecraftforge.debug.misc;

import net.minecraftforge.common.capabilities.CapabilityReference;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.items.IItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("capability_reference_test")
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = "capability_reference_test")
public class CapabilityReferenceTest
{

    private static final CapabilityReference<IItemHandler> CAP_REF = CapabilityReference.create();
    private static final CapabilityReference<MustNotLoad> CAP_REF2 = CapabilityReference.create();
    private static final Logger LOGGER = LoggerFactory.getLogger("CapabilityReferenceTest");

    @SubscribeEvent
    public static void commonSetupHandler(FMLCommonSetupEvent event)
    {
        LOGGER.info("CAP_REF: {}", CAP_REF.resolve().orElse(null));
        LOGGER.info("CAP_REF2: {}", CAP_REF2.resolve().orElse(null));
    }

    static class MustNotLoad
    {
        static
        {
            // javac doesn't allow just throwing an exception
            if (true) throw new RuntimeException("CapabilityReference soft reference failed.");
        }
    }

}
