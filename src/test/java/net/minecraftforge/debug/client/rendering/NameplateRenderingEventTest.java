package net.minecraftforge.debug.client.rendering;

import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("nameplate_render_test")
@Mod.EventBusSubscriber
public class NameplateRenderingEventTest
{

    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onNameplateRender(RenderNameplateEvent event)
    {

        if(!ENABLED)
        {
            return;
        }

        if(event.getEntity() instanceof CowEntity)
        {
            event.setContent(TextFormatting.RED + "Evil Cow");
            event.setResult(Event.Result.ALLOW);
        }

        if(event.getEntity() instanceof PlayerEntity)
        {
            event.setContent(TextFormatting.GOLD + "" + (event.getEntity()).getDisplayName().getString());
        }
    }
}
