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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod(modid = LootTableTest.MODID, name = "Loot Table Debug", version = "1.0", acceptableRemoteVersions = "*")
public class LootTableTest
{
    public static final String MODID = "loot_table_debug";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void lootLoad(LootTableLoadEvent event)
    {
        if (!event.getName().equals(LootTableList.CHESTS_SPAWN_BONUS_CHEST))
        {
            return;
        }

        // Remove axes and replace with chestpeice, First vanilla entry is always called "main"
        LootPool main = event.getTable().getPool("main"); //Note: This CAN NPE if another mod removes things
        main.removeEntry("minecraft:wooden_axe");
        main.removeEntry("minecraft:stone_axe");
        main.addEntry(new LootEntryItem(Items.DIAMOND_CHESTPLATE, 1, 0, new LootFunction[0], new LootCondition[0], MODID + ":diamond_chestplate"));

        // Get rid of all building mats. Which is pool #3, index starts at 0, but 0 is named "main"
        event.getTable().removePool("pool3");
    }

    @net.minecraftforge.eventbus.api.SubscribeEvent
    public void lootingEvent(LootingLevelEvent event)
    {
        // if the player shoots something with a projectile, use looting 3
        DamageSource damageSource = event.getDamageSource();
        if (damageSource.isProjectile() && damageSource.getTrueSource() instanceof EntityPlayer && damageSource.getImmediateSource() instanceof EntityArrow)
        {
            event.setLootingLevel(3);
        }
    }
}
