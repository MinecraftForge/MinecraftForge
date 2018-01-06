package net.minecraftforge.debug;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = LivingAttackEventTest.MODID, name = LivingAttackEventTest.NAME, version = "1.0.0", acceptableRemoteVersions = "*")
public class LivingAttackEventTest {

    public static final String MODID = "livingattackeventtest";
    public static final String NAME = "LivingAttackEventTest";
    private static final Logger LOGGER = LogManager.getLogger(NAME);

    @EventBusSubscriber
    public static class LivingAttackEventHandler {

        @SubscribeEvent
        public static void onLivingAttack(LivingAttackEvent event) {
            if (event.getSource() == DamageSource.ANVIL) {
                LOGGER.info("{} was hit by an anvil!", event.getEntityLiving().getName());
            }
        }
    }
}