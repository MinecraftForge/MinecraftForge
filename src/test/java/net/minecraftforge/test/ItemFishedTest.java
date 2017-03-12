package net.minecraftforge.test;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;

import java.util.List;

@Mod(modid = "itemfishtest", name = "ItemFishTest", version = "1.0.0")
public class ItemFishedTest
{

    private static final boolean ENABLE = false;
    private static Logger logger;

    @Mod.EventHandler
    public void onInit(FMLPreInitializationEvent event)
    {
        if (ENABLE)
        {
            logger = event.getModLog();
            logger.info("Enabling Fishing Test mod");
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onItemFished(ItemFishedEvent event)
    {
        logger.info("Item fished");
        List<ItemStack> stacks = event.getItemStacks();
        stacks.clear();
        stacks.add(new ItemStack(Items.SLIME_BALL, 2));
        stacks.add(new ItemStack(Items.SNOWBALL, 3));
        event.setRodDamage(50);
    }
}
