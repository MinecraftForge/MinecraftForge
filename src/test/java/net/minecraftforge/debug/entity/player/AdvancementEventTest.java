package net.minecraftforge.debug.entity.player;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementEarnEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.AdvancementProgressEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent.ProgressType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(AdvancementEventTest.MOD_ID)
public class AdvancementEventTest
{
    public static final String MOD_ID = "advancement_event";
    public static final boolean ENABLED = false;
    public static final Logger LOGGER = LogManager.getLogger();

    public AdvancementEventTest(){
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        if(ENABLED)
        {
            forgeBus.addListener(this::onAdvancementEarnEvent);
            forgeBus.addListener(this::onAdvancementProgressEvent);
        }
    }

    public void onAdvancementEarnEvent(AdvancementEarnEvent event)
    {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();
        LOGGER.info("Player {} earned advancement {} and was awarded {}",player,advancement.getId(), advancement.getRewards().toString());
    }
    public void onAdvancementProgressEvent(AdvancementProgressEvent event)
    {
        Advancement advancement = event.getAdvancement();
        Player player = event.getPlayer();
        AdvancementProgress advancementProgress = event.advancementProgress;
        String criterion = event.criterion;
        AdvancementEvent.ProgressType progressType= event.progressType;
        String action;
        if(progressType == ProgressType.GRANT){
            action = "granted";
        }else{
            action = "revoked";
        }
        LOGGER.info("Player {} progressed advancement {}. It was {} progress on {} criterion. It has completed {}% of the achievement",player,advancement.getId(),action, criterion,advancementProgress.getPercent()*100);
    }
}
