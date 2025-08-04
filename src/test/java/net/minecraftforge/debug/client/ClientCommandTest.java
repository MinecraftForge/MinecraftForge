/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.gametest.GameTest;
import net.minecraftforge.gametest.GameTestNamespace;
import net.minecraftforge.test.BaseTestMod;

import java.util.Random;

import com.mojang.brigadier.arguments.IntegerArgumentType;

@GameTestNamespace("forge")
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
