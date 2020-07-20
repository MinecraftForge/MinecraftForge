package net.minecraftforge.debug.entity.player;

import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("player_name_event_test")
@Mod.EventBusSubscriber()
public class PlayerNameEventTest
{
    private static final boolean ENABLE = false;

    @SubscribeEvent
    public static void onPlayerNameEvent(PlayerEvent.NameFormat event)
    {
        if (!ENABLE) return;
        event.setDisplaynameComponent(new StringTextComponent(TextFormatting.RED + "Test Name")); //New version
        event.setDisplayname(TextFormatting.AQUA + "Test Name"); //Old version, both work
    }
}
