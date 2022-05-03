/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client.rendering;

import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
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

        if(event.getEntity() instanceof CowEntity)
        {
            event.setContent(new StringTextComponent("Evil Cow").withStyle(TextFormatting.RED));
            event.setResult(Event.Result.ALLOW);
        }

        if(event.getEntity() instanceof PlayerEntity)
        {
            event.setContent(event.getEntity().getDisplayName().copy().withStyle(TextFormatting.GOLD));
        }
    }
}
