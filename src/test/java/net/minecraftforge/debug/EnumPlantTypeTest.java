package net.minecraftforge.debug;

import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Mod(modid = "enumplanttypetest")
public class EnumPlantTypeTest 
{
	private static Logger LOGGER = LogManager.getLogger();
	
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
			LOGGER.warn("EnumHelper in BiomeType is working incorrectly!", npe);
		}
		finally
		{
		    if (biomeType == null || !biomeType.name().equals("FAKE")) 
			    LOGGER.warn("EnumHelper in BiomeType is working incorrectly!");
		}
	    EnumPlantType plantType = null;
	    if (plantType == null || !plantType.name().equals("FAKE"))
	    	;
	    try 
		{ 
	    	 plantType = EnumPlantType.getPlantType("FAKE");
		} 
		catch (NullPointerException npe)
		{
			LOGGER.warn("EnumHelper in EnumPlantType is working incorrectly!", npe);
		}
		finally
		{
		    if (plantType == null || !plantType.name().equals("FAKE")) 
		    	LOGGER.warn("EnumHelper in EnumPlantType is working incorrectly!");
		}
	}
}
