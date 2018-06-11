/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.debug.gameplay.loot;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(modid = LootTableLoadEventTest.MODID, name = "LootTableLoadEventTest", version = "1.0", acceptableRemoteVersions = "*")
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
