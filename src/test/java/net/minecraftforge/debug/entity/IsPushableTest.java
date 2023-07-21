package net.minecraftforge.debug.entity;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(IsPushableTest.MODID)
public class IsPushableTest {

    public static final boolean ENABLE = false;
    public static final String MODID = "is_pushable_test";

    public IsPushableTest()
    {
        if (ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void pushAttempt(EntityEvent.IsPushable event)
    {
        if (event.getEntity() instanceof Player player)
        {
            event.setPushable(false);
        }
    }
}
