package net.minecraftforge.test;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

@Mod(modid = "itemfishtest", name = "ItemFishTest", version = "1.0.0")
public class ItemFishedTest {

    private static final boolean ENABLE = true;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event)
    {
        if (ENABLE)
        {
            System.out.println("Enabling Fishing Test mod");
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onItemFished(ItemFishedEvent event)
    {
        System.out.println("Item fished");
        List<ItemStack> stacks = event.getItemStacks();
        stacks.clear();
        stacks.add(new ItemStack(Items.SLIME_BALL, 2));
        stacks.add(new ItemStack(Items.SNOWBALL, 3));
        event.setRodDamage(50);
    }
}
