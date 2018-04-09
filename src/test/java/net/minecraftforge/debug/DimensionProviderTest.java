package net.minecraftforge.debug;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.DimensionProvider;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import org.apache.logging.log4j.Logger;

@Mod(modid = DimensionProviderTest.MODID, name = "ForgeDimensionTest", version = DimensionProviderTest.VERSION, acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class DimensionProviderTest
{

    public static final boolean ENABLED = false;
    public static final String MODID = "forgedimensiontest";
    public static final String VERSION = "1.0";
    private static Logger logger;
    public static final ResourceLocation OVERWORLD_ID = new ResourceLocation("minecraft:overworld");
    public static final ResourceLocation DIM1_ID = new ResourceLocation("forgedimensiontest:pocketDimension");
    public static final ResourceLocation DIM2_ID = new ResourceLocation("forgedimensiontest:lavaDimension");
    public static final ResourceLocation DIM3_ID = new ResourceLocation(MODID, "providerdimension");

    public static DimensionType providerTestType = null;
    public static DimensionProvider customProviderDimension;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLED)
            return;

        logger = event.getModLog();
    }

    @SubscribeEvent
    public static void registerDimensions(RegistryEvent.Register<DimensionProvider> event)
    {
        if (!ENABLED)
            return;

        customProviderDimension = DimensionProvider.dimensionWithCustomType(
                "providerdimension",
                "providertesttype",
                "_providertest",
                FactoryWorldProviderTest.class,
                false
        );

        providerTestType = customProviderDimension.getType();
        event.getRegistry().register(customProviderDimension);
        event.getRegistry().register(new DimensionProvider(DimensionType.OVERWORLD, "forgedimensiontest:pocketDimension"));
        event.getRegistry().register(new DimensionProvider(DimensionType.NETHER, "lavaDimension"));    //test active mod container
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        if (!ENABLED)
            return;

        logger.info("overworld works: {}", ForgeRegistries.DIMENSIONS.containsKey(OVERWORLD_ID));
        logger.info("1st dimension registered {}, int assigned is {}", ForgeRegistries.DIMENSIONS.containsKey(DIM1_ID),
                ForgeRegistries.DIMENSIONS.getValue(DIM1_ID).getDimIntID());
        logger.info("2nd dimension registered {}, int assigned is {}", ForgeRegistries.DIMENSIONS.containsKey(DIM2_ID),
                ForgeRegistries.DIMENSIONS.getValue(DIM2_ID).getDimIntID());
        logger.info("custom dimension id is {}", ForgeRegistries.DIMENSIONS.getKey(customProviderDimension));
    }

    public static class FactoryWorldProviderTest extends WorldProvider
    {

        @Override
        public DimensionType getDimensionType()
        {
            return DimensionProviderTest.providerTestType;
        }

        @Override
        public double getMovementFactor()
        {
            return 0.5D;
        }

    }

}
