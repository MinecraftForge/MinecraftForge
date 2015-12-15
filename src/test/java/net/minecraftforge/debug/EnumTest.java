package net.minecraftforge.debug;

import net.minecraftforge.common.BiomeManager.BiomeType;
import net.minecraftforge.common.SoilType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@Mod(modid = "enumtest")
public class EnumTest 
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
	    SoilType plantType = null;
	    try 
		{ 
	    	 plantType = SoilType.getType("FAKE");
		}
		catch (NullPointerException npe)
		{
			LOGGER.warn("SoilType.getSoilType is working incorrectly!", npe);
		}
		finally
		{
		    if (plantType == null || !plantType.getName().equals("FAKE")) 
		    	LOGGER.warn("SoilType.getSoilType is working incorrectly!");
		}
	}
}
