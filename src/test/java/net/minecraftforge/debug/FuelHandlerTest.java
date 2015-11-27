package net.minecraftforge.debug;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = "FuelHandlerTest")
public final class FuelHandlerTest
{
    private static final boolean ENABLED = false;
    
    @Mod.Instance("FuelHandlerTest")
    public static FuelHandlerTest instance;
    
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLED)
        {
            GameRegistry.registerFuelHandler(new IFuelHandler() {
                @Override
                public int getBurnTime(ItemStack fuel)
                {
                    //disallow to burn coal
                    if (fuel.getItem() == Items.coal)
                    {
                        return -1;
                    }
                    
                    //wooden tools are powerful
                    if (fuel.getItem() instanceof ItemTool && ((ItemTool) fuel.getItem()).getToolMaterialName().equals("WOOD"))
                    {
                        return 10000;
                    }
                    
                    //arrow burns one item
                    if (fuel.getItem() == Items.arrow)
                    {
                        return 200;
                    }
                    
                    return 0;
                }
            });
            
            GameRegistry.registerFuelHandler(new IFuelHandler() {
                @Override
                public int getBurnTime(ItemStack fuel)
                {                    
                    //arrow burns half a item, override the previous one
                    if (fuel.getItem() == Items.arrow)
                    {
                        return 100;
                    }
                    
                    return 0;
                }
            });
        }
    }
}
