package net.minecraftforge.debug;

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
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = LootTablesDebug.MODID, name = "Loot Table Debug", version = "1.0", acceptableRemoteVersions = "*")
public class LootTablesDebug
{
    public static final String MODID = "loot_table_debug";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
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

    @SubscribeEvent
    public void lootingEvent(LootingLevelEvent event)
    {
        // if the player shoots something with a projectile, use looting 3
        DamageSource damageSource = event.getDamageSource();
        if (damageSource.isProjectile() && damageSource.getEntity() instanceof EntityPlayer && damageSource.getSourceOfDamage() instanceof EntityArrow)
        {
            event.setLootingLevel(3);
        }
    }
}
