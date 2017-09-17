package net.minecraftforge.debug;

import net.minecraft.entity.passive.EntityCow;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.BabyEntitySpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = BreedingTest.MODID, name = "BreedingTest", version = "1.0", acceptableRemoteVersions = "*")
public class BreedingTest
{
    public static final String MODID = "breedingtest";
    static final boolean ENABLED = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onBabyBorn(BabyEntitySpawnEvent event)
    {
        event.setChild(new EntityCow(event.getParentA().world));
    }
}
