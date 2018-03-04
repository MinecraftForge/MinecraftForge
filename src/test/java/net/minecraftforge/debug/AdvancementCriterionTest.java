package net.minecraftforge.debug;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.PositionTrigger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(name = "advancementcriteriontest", modid = "advancementcriteriontest", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class AdvancementCriterionTest {
    private static final PositionTrigger TRIGGER = new PositionTrigger(new ResourceLocation("advancementcriteriontest", "position"));

    @Mod.EventHandler
    public void preinit(FMLPreInitializationEvent evt)
    {
        CriteriaTriggers.register(TRIGGER);
    }

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent evt)
    {
        if (evt.side == Side.SERVER && evt.phase == TickEvent.Phase.END)
        {
            TRIGGER.trigger((EntityPlayerMP) evt.player);
        }
    }
}
