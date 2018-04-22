package net.minecraftforge.debug;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

@Mod(modid = "professiontest", name = "ProfessionTest2000", version = "1.0", acceptableRemoteVersions = "*")
@EventBusSubscriber
public class VillagerProfessionTest
{
    @SubscribeEvent
    public static void registerVillagers(RegistryEvent.Register<VillagerProfession> event)
    {
        VillagerProfession profession = new VillagerProfession("professiontest:test_villager", "professiontest:textures/entity/test_villager.png", "professiontest:textures/entity/zombie_test_villager.png");
        new VillagerCareer(profession, "professiontest:test_villager");
        event.getRegistry().register(profession);
    }
}
