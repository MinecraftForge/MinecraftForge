package net.minecraftforge.debug;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Debug mod that sets the burn time of every item to 0
 */
@Mod(modid = "fuelhandlertest", name = "Guel Handler Test", version = "0.0.0")
public class FuelHandlerTest {
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        GameRegistry.registerFuelHandler(new IFuelHandler() {
            @Override
            public int getBurnTime(ItemStack fuel) {
                return 0;
            }
    
            @Override
            public boolean matches(ItemStack stack) {
                return true;
            }
        });
    }
}
