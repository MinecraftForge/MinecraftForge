package net.minecraftforge.debug;

import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.Logger;

@Mod(modid = "enumplanttypetest", name = "EnumPlantTypeTest", version = "1.0", acceptableRemoteVersions = "*")
public class EnumPlantTypeTest
{
    private static Logger logger;

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        BiomeType biomeType = null;
        try
        {
            biomeType = BiomeType.getType("FAKE");
        }
        catch (NullPointerException npe)
        {
            logger.warn("EnumHelper in BiomeType is working incorrectly!", npe);
        }
        finally
        {
            if (biomeType == null || !biomeType.name().equals("FAKE"))
            {
                logger.warn("EnumHelper in BiomeType is working incorrectly!");
            }
        }
        EnumPlantType plantType = null;
        try
        {
            plantType = EnumPlantType.getPlantType("FAKE");
        }
        catch (NullPointerException npe)
        {
            logger.warn("EnumHelper in EnumPlantType is working incorrectly!", npe);
        }
        finally
        {
            if (plantType == null || !plantType.name().equals("FAKE"))
            {
                logger.warn("EnumHelper in EnumPlantType is working incorrectly!");
            }
        }
    }
}
