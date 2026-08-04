/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

/*


package net.minecraftforge.debug.gameplay.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.event.FMLInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

//@Mod(modid = LootTableLoadEventTest.MODID, name = "LootTableLoadEventTest", version = "1.0", acceptableRemoteVersions = "*")
public class LootTableLoadEventTest
{
    public static final boolean ENABLED = false;
    public static final String MODID = "loottable_load_event_test";

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void onLootTableLoadEvent(LootTableLoadEvent event)
    {
        if (LootTableList.CHESTS_SPAWN_BONUS_CHEST.equals(event.getName()))
        {
            ResourceLocation loc = new ResourceLocation(MODID, "chests/custom_spawn_bonus_chest");
            LootTable customLootTable = event.getLootTableManager().getLootTableFromLocation(loc);
            event.setTable(customLootTable);
        }
    }
}
*/
