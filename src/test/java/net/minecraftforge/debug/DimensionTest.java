package net.minecraftforge.debug;

import org.apache.logging.log4j.Logger;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.Dimension;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

@Mod(modid = DimensionTest.MODID, name = "ForgeDimensionTest", version = DimensionTest.VERSION, acceptableRemoteVersions = "*")
public class DimensionTest {
	
	public static final boolean ENABLED = true;
	public static final String MODID = "forgedimensiontest";
	public static final String VERSION = "1.0";
	private static Logger logger;
	public static final ResourceLocation OVERWORLD_ID = new ResourceLocation("minecraft:overworld");
	
	
	@EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (!ENABLED)
            return;

        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(DimensionTest.class);
    }
	
	@SubscribeEvent
	public static void registerDimensions(RegistryEvent.Register<Dimension> event)
	{
		if(!ENABLED)
			return;
		
		logger.info("attempting registration of Dimensions");
		event.getRegistry().register(new Dimension(DimensionType.OVERWORLD,"forgedimensiontest:pocketDimension"));
		event.getRegistry().register(new Dimension(DimensionType.NETHER,"lavaDimension"));
	}
	
	@EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        if (!ENABLED)
            return;
 
        logger.info("overworld works: {}", ForgeRegistries.DIMENSIONS.containsKey(OVERWORLD_ID));
        logger.info("1st dimension registered {}", ForgeRegistries.DIMENSIONS.containsKey(new ResourceLocation("forgedimensiontest:pocketDimension")));
        logger.info("2nd dimension registered {}", ForgeRegistries.DIMENSIONS.containsKey(new ResourceLocation("forgedimensiontest:lavaDimension")));
    }

}
