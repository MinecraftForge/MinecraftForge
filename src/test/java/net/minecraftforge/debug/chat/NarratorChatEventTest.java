/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.chat;

import net.minecraft.client.NarratorStatus;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.client.event.NarratorChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

/**
 * Tests {@link NarratorChatEvent} by replacing the narration message for chat messages and the single success reply of
 * {@link net.minecraft.server.commands.EffectCommands /effect}.
 *
 * <p>To observe the effects of this test, enable the Narrator using the {@code CTRL + B} keyboard shortcut, or through
 * the Accessibility sub-menu of the Options screen. Both effects can be observed when the narrator is set to
 * {@linkplain NarratorStatus#ALL all messages} or the specified narrator setting.</p>
 *
 * <p>The first effect requires that the Narrator be able to read chat messages by players ({@link NarratorStatus#CHAT}).
 * When a player sends any regular chat message, such as the {@code Dev} player saying "Hello", the narrator will read
 * "The person Dev says Hello".</p>
 *
 * <p>The second effect requires that the Narrator be able to read system messages ({@link NarratorStatus#SYSTEM}).
 * When the {@code /effect} command is ran to apply a single effect to a single target, such as for example the
 * Wither effect to the single player named {@code Dev} using {@code /effect give Dev minecraft:wither}, the narrator
 * will read "Blessed someone named Dev with the effect of Wither".</p>
 */
@Mod("narrator_chat_event_test")
public class NarratorChatEventTest
{
    static final boolean ENABLED = true;

    public NarratorChatEventTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.addListener(this::onNarratorChat);
        }
    }

    public void onNarratorChat(NarratorChatEvent event)
    {
        if (event.getMessage() instanceof TranslatableComponent message)
        {
            // Checks normal event behavior
            // commands.effect.give.success.single: Applied effect %s to %s
            if (message.getKey().equals("commands.effect.give.success.single"))
            {
                event.setMessage(new TranslatableComponent("Blessed someone named %2$s with the effect of %1$s", message.getArgs()));
            }

            // Check that we can change a chat message, overriding Minecraft's hardcoded logic for it
            // chat.type.text: <%s> %s
            if (message.getKey().equals("chat.type.text"))
            {
                event.setMessage(new TranslatableComponent("The person %s says %s", message.getArgs()));
            }
        }
    }
}
