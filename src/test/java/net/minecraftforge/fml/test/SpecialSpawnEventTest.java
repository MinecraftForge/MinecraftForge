package net.minecraftforge.fml.test;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = SpecialSpawnEventTest.MOD_ID, version = "1.0", acceptableRemoteVersions="*")
public class SpecialSpawnEventTest {
    private static final boolean ENABLED = true; // TODO FALSE
    public static final String MOD_ID = "spawnerduratest";

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent e)
    {
        if (ENABLED) {
            MinecraftForge.EVENT_BUS.register(this);
        }

    }

    @SubscribeEvent
    public void specialSpawnEvent(LivingSpawnEvent.SpecialSpawn e) {
        MobSpawnerBaseLogic spawner = e.getSpawner();
        if (spawner == null) return;
        if (spawner.getSpawnerEntity() != null) {
            Entity spawn = new EntityChicken(e.getWorld());
            spawn.copyLocationAndAnglesFrom(spawner.getSpawnerEntity());
            e.getWorld().spawnEntity(spawn);
            spawner.getSpawnerEntity().setDead();
        } else {
             e.getWorld().setBlockState(spawner.getSpawnerPosition(), Blocks.FIRE.getDefaultState());
        }
    }
}
