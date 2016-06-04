package net.minecraftforge.test;

import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingPotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Basic implementation for testing living potion events hooks
 */

@Mod(modid="livingpotioneventstest", name="Living Potion Events Test", version="0.0.0")
public class LivingPotionEventsTest {
    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    /*
     * Test of the main LivingPotion event
     */
    @SubscribeEvent
    public void onPotionEvent(LivingPotionEvent event) {
        System.out.println(event.getEntity().getName() + " had a potion event occur regarding " + event.getEffect().getEffectName());
    }

    /*
     * Test of the LivingPotionAdded event
     */
    @SubscribeEvent
    public void onPotionAdded(LivingPotionEvent.LivingPotionAddedEvent event) {
        System.out.println(event.getEntity().getName() + " about to receive " + event.getEffect().getEffectName());

        if(event.getEffect().getPotion() == Potion.REGISTRY.getObject(new ResourceLocation("minecraft:luck"))) {
            System.out.println("Preventing luck from being applied from LivingPotionEventsTest.onPotionAdded.");
            event.setCanceled(true);
        }
    }

    /*
     * Test of the LivingPotionCuredEvent
     */
    @SubscribeEvent
    public void onPotionCured(LivingPotionEvent.LivingPotionCuredEvent event) {
        System.out.println(event.getEntity().getName() + " about to cure " + event.getEffect().getEffectName() +
            " with " + event.getCurativeItem().getDisplayName());

        if(event.getCurativeItem().getItem() == Items.MILK_BUCKET && event.getEntity().getEntityWorld().rand.nextDouble() < 0.5) {
            System.out.println("Preventing this being cured by milk from LivingPotionEventsTest.onPotionCured.");
            event.setCanceled(true);
        }
    }

    /*
     * Test of the LivingPotionRemoved event
     */
    @SubscribeEvent
    public void onPotionRemoved(LivingPotionEvent.LivingPotionRemovedEvent event) {
        System.out.println(event.getEntity().getName() + " had " + event.getEffect().getEffectName() + " removed " +
                (event.isByCommand() ? "via command." : "directly."));
    }

    /*
     * Test of the LivingPotionExpired event
     */
    @SubscribeEvent
    public void onPotionExpired(LivingPotionEvent.LivingPotionExpiredEvent event) {
        System.out.println(event.getEntity().getName() + " had " + event.getEffect().getEffectName() + " expire.");
    }
}

