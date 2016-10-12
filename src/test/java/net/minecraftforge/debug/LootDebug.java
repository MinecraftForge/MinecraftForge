package net.minecraftforge.debug;

import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootEntry;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootGenerateEvent;
import net.minecraftforge.event.LootTableLoadEvent;
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

    @SubscribeEvent
    public void fishingHook(LootTableLoadEvent evt)
    {
        System.out.println(evt.getPath().toString());
        if ("minecraft:gameplay/fishing".equals(evt.getPath().toString()))
        {
            evt.addPool(new LootPool(new LootEntry[]{
                    new LootEntryItem(Items.cooked_porkchop, 200, 1, new LootFunction[0], new LootCondition[0])
            }, new LootCondition[0], new RandomValueRange(3), new RandomValueRange(0)));
        }
    }

    // Test that we can load mod loot tables from our own domain
    @SubscribeEvent
    public void handle(MinecartInteractEvent evt) {
        if (!evt.minecart.worldObj.isRemote && evt.minecart instanceof EntityMinecartChest
                && "LOOTTEST".equals(evt.minecart.getCustomNameTag())) {
            ((EntityMinecartChest) evt.minecart).setLootTable(new ResourceLocation("forgelootdebug", "test_table"), 0);
        }
    }

    // Test the loot event
    @SubscribeEvent
    public void onLoot(LootGenerateEvent evt) {
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
