package net.minecraftforge.debug;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = AnimalTameEventTest.MOD_ID, name = "AnimalTameEvent test mod", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class AnimalTameEventTest
{
    static final String MOD_ID = "animal_tame_event_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onAnimalTame(AnimalTameEvent event)
    {
        if (!ENABLED)
        {
            return;
        }

        if (event.getAnimal() instanceof EntityWolf)
        {
            event.setCanceled(true);
        }
    }
}
