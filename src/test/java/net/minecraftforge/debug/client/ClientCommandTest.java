/*
 * Copyright (c) singularity Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftsingularity.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftsingularity.client.event.RegisterClientCommandsEvent;
import net.minecraftsingularity.fml.common.Mod;
import net.minecraftsingularity.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftsingularity.gametest.GameTest;
import net.minecraftsingularity.gametest.GameTestNamespace;
import net.minecraftsingularity.test.BaseTestMod;

import java.util.Random;

import com.mojang.brigadier.arguments.IntegerArgumentType;

@GameTestNamespace("singularity")
@Mod(ClientCommandTest.MODID)
public class ClientCommandTest extends BaseTestMod {
    public static final String MODID = "client_command";
    private static final String COMMAND = "testClientCommand";
    private static int LAST_COMMAND = -1;

    public ClientCommandTest(FMLJavaModLoadingContext context) {
        super(context, false, false);
        RegisterClientCommandsEvent.BUS.addListener(this::addCommand);
    }

    private void addCommand(RegisterClientCommandsEvent event) {
        event.getDispatcher()
            .register(Commands.literal(COMMAND)
                .then(Commands.argument("id",  IntegerArgumentType.integer())
                    .executes(ctx -> LAST_COMMAND = IntegerArgumentType.getInteger(ctx, "id"))
                )
            );
    }

    @GameTest
    public static void testCommand(GameTestHelper helper) throws IllegalAccessException {
        int random = new Random().nextInt();
        Minecraft.getInstance().getConnection().sendUnattendedCommand(COMMAND + ' ' + random, null);
        helper.assertTrue(LAST_COMMAND == random, "Client command did not execute");
        helper.succeed();
    }
}
