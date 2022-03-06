/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.api.distmarker.Dist;


@Mod(NameplateRenderingEventTest.MODID)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class NameplateRenderingEventTest
{
    public static final String MODID = "nameplate_render_test";
    static final boolean ENABLED = false;

    @SubscribeEvent
    public static void onNameplateRender(RenderNameplateEvent event)
    {

        if(!ENABLED)
        {
            return;
        }

        if(event.getEntity() instanceof Cow)
        {
            event.setContent(new TextComponent("Evil Cow").withStyle(ChatFormatting.RED));
            event.setResult(Event.Result.ALLOW);
        }

        if(event.getEntity() instanceof Player)
        {
            event.setContent(event.getEntity().getDisplayName().copy().withStyle(ChatFormatting.GOLD));
        }
    }
}
