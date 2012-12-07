package net.minecraftforge.test.event;

import java.util.Calendar;

import net.minecraft.src.Block;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityCraftMatchEvent;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Minecraft Forge EntityCraftMatch Event Test #1
 * 
 * @author mrTJO
 * @since 1.4.5
 * @version 0.1
 */
@Mod(modid="ForgeTE.EVT-01", name="Forge Test - EntityCraftMatch Event Test #1", version="0.1")
public class TestEntityCraftMatch
{
    @PreInit
    public void onPreInit(FMLPreInitializationEvent evt)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    @ForgeSubscribe
    public void onEntityCraftMatch(EntityCraftMatchEvent evt)
    {
        // Allow Stick Craft In-game Daytime Only
        if (evt.getItemResult().getItem() == Item.stick)
        {
            if (evt.getWorld().getWorldInfo().getWorldTime() >= 12500)
                evt.denyResult();
        }
        
        // Deny Button Craft Between 19:00 and 6:00
        if (evt.getItemResult().itemID == Block.woodenButton.blockID)
        {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            if (hour < 6 || hour > 18)
                evt.denyResult();
        }
        
        // Transform Workbench Craft into Golden Apple
        if (evt.getItemResult().itemID == Block.workbench.blockID)
        {
            evt.setItemResult(new ItemStack(Item.appleGold));
        }
    }
}
