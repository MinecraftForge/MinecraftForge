/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.permission.PermissionAPI;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("permissiontest")
public class PermissionTest
{
    private static final boolean ENABLED = true;
    private static final Logger LOGGER = LogManager.getLogger();

    private static final PermissionNode<Boolean> boolPerm = new PermissionNode<>("permissiontest", "test.blob", PermissionTypes.BOOLEAN, (player, playerUUID, context) -> true);
    private static final PermissionNode<String> stringPerm = new PermissionNode<>("permissiontest", "test.blobText", PermissionTypes.STRING, (player, playerUUID, context) -> "Hello World");
    private static final PermissionNode<Integer> intPerm = new PermissionNode<>("permissiontest", "test.blob.integer", PermissionTypes.INTEGER, (player, playerUUID, context) -> 3);
    private static final PermissionNode<Component> componentPerm = new PermissionNode<>("permissiontest", "test.blob.component", PermissionTypes.COMPONENT, (player, playerUUID, context) -> Component.literal("This is a component"));

    private static final PermissionNode<Boolean> unregisteredPerm = new PermissionNode<>("permissiontest", "test.unregistered", PermissionTypes.BOOLEAN, (player, playerUUID, context) -> false);


    public PermissionTest()
    {
        if (ENABLED)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void registerNodes(PermissionGatherEvent.Nodes event)
    {
        event.addNodes(boolPerm, stringPerm, intPerm, componentPerm);
    }

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        event.getDispatcher().register(Commands.literal("permtest")
            .requires(src -> canUseCommand(src, boolPerm))
            .executes(context -> {
                context.getSource().sendSuccess(() -> Component.literal("Blob"), false);
                context.getSource().sendSuccess(() -> Component.literal("String:" + PermissionAPI.getPermission((ServerPlayer) context.getSource().getEntity(), stringPerm)), false);
                context.getSource().sendSuccess(() -> Component.literal("Int: " + PermissionAPI.getPermission((ServerPlayer) context.getSource().getEntity(), intPerm)), false);
                context.getSource().sendSuccess(() -> PermissionAPI.getPermission((ServerPlayer) context.getSource().getEntity(), componentPerm), false);

                return 1;
            }));

        //This command should not be usable as the command isn't registered.
        //It should also print an error
        event.getDispatcher().register(Commands.literal("permtesterr")
            .requires(src -> canUseCommand(src, unregisteredPerm))
            .executes(context -> {
                context.getSource().sendSuccess(() -> Component.literal("Blob"), false);
                return 1;
            }));
    }

    /**
     * Simple utility to catch exceptions with the test commands above.
     * Without that, the expected UnregisteredPermissionNode exception, triggers further exceptions and therefore isn't visible anymore.
     * This is only required to handle the intended error in the permission API, and should not be necessary with correct use.
     */
    private static boolean canUseCommand(CommandSourceStack src, PermissionNode<Boolean> booleanPermission, PermissionDynamicContext<?>... context)
    {
        try
        {
            return src.getEntity() != null && src.getEntity() instanceof ServerPlayer && PermissionAPI.getPermission((ServerPlayer) src.getEntity(), booleanPermission, context);
        } catch (Exception e)
        {
            LOGGER.error(e.getMessage());
            return false;
        }
    }
}
