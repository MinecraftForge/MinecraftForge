package net.minecraftforge.testmods;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(LightTestMod.ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class LightTestMod {

    final static String ID = "lightfixtest";

    private static final DynamicLightBlock BLOCK = new DynamicLightBlock();

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> e) {
        e.getRegistry().register(BLOCK);
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> e) {
        e.getRegistry().register(BLOCK.asItem());
    }

}
