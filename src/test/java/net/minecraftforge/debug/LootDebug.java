package net.minecraftforge.debug;

import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.minecart.MinecartInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "forgelootdebug")
public class LootDebug {

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Test that we can load mod loot tables from our own domain
    @SubscribeEvent
    public void handle(MinecartInteractEvent evt) {
        if (evt.minecart instanceof EntityMinecartChest
                && "LOOTTEST".equals(evt.minecart.getCustomNameTag())) {
            ((EntityMinecartChest) evt.minecart).setLootTable(new ResourceLocation("forgelootdebug", "test_table"), 0);
        }
    }

    // Test the loot event
    @SubscribeEvent
    public void onLoot(LootEvent evt) {
        if (evt.getContext().getLootedEntity() instanceof EntityCreeper
                && "LOOTTEST".equals(evt.getContext().getLootedEntity().getCustomNameTag())) {
            evt.getLoot().add(new ItemStack(Blocks.stone));
        }

        if (evt.getContext().getLootedEntity() instanceof EntitySkeleton
                && "LOOTTEST".equals(evt.getContext().getLootedEntity().getCustomNameTag())) {
            evt.setCanceled(true);
        }
    }

    // Make sure old event still works
    @SubscribeEvent
    public void onDrops(LivingDropsEvent evt) {
        if (evt.entity instanceof EntitySkeleton
                && "LOOTTEST".equals(evt.entity.getCustomNameTag())) {
            System.out.println("Should have no arrows or bones: " + evt.drops);
        }
    }

}
