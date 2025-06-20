/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import java.lang.reflect.Field;
import java.util.List;

/**
 * run /testClickEvent to create a clickable chat message to print player locations.
 * run /playerPos to run the client command to print player location
 */
@GameTestNamespace("forge")
@Mod(ClientCommandTest.MODID)
public class ClientCommandTest extends BaseTestMod {
    public static final String MODID = "client_command_test";

    public ClientCommandTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        RegisterClientCommandsEvent.BUS.addListener(this::addCommand);
    }

    private void addCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher().
                register(Commands.literal("playerPos")
                        .executes(ctx -> {
                                    this.printLoc();
                                    return 1;
                                }
                        ));
        event.getDispatcher().
                register(Commands.literal("testClickEvent")
                        .executes(ctx -> {
                                    this.testClickEvent();
                                    return 1;
                                }
                        ));
    }

    @GameTest(name = MODID)
    public static void testCommand(GameTestHelper helper) throws IllegalAccessException {
        var mc = Minecraft.getInstance();
        var packetHandler = mc.getConnection();
        packetHandler.sendUnattendedCommand("playerPos", null);
        var storeState = mc.gui.getChat().storeState();
        Field messageField = storeState.getClass().getDeclaredFields()[0];
        messageField.setAccessible(true);
        List<GuiMessage> messages = (List<GuiMessage>) messageField.get(storeState);
        List<String> messageStrings = messages.stream().map(guiMessage -> guiMessage.content().getString()).toList();
        var success = messageStrings.contains(mc.player.position().toString());
        helper.assertTrue(success, "Client command executed successfully");
        helper.succeed();
    }

    private void testClickEvent() {
        MutableComponent clickable = Component.literal("Click §b§nHere§r to print your location");
        clickable.withStyle(style -> style.withClickEvent(new ClickEvent.RunCommand("/playerPos")));
        MutableComponent hover = Component.literal("Click Here to print out your location");
        hover.withStyle(style -> style.withColor(net.minecraft.network.chat.TextColor.fromLegacyFormat(ChatFormatting.YELLOW)));
        clickable.withStyle(style -> style.withHoverEvent(new HoverEvent.ShowText(hover)));
        Minecraft.getInstance().gui.getChat().addMessage(clickable);
    }

    private void printLoc() {
        var player = Minecraft.getInstance().player;
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal(player.position().toString()));
    }
}
