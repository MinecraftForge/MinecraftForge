package net.minecraftforge.debug;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;


/**
 * Debug mod that sets the burn time of every item to 0
 */
@Mod(modid = "fuelhandlertest", name = "Fuel Handler Test", version = "0.0.0", acceptableRemoteVersions = "*")
public class FuelHandlerTest {
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.registerFuelHandler(fuel -> 0);
    }
}
