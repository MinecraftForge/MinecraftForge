package net.minecraftforge.debug;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.OptionalInt;

/**
 * Debug mod that sets the burn time of every item to 0
 */
@Mod(modid = "fuelhandlertest", name = "Guel Handler Test", version = "0.0.0")
public class FuelHandlerTest {
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.registerFuelHandler(fuel -> 0);
    }
}
