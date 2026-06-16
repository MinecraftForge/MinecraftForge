/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.client;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;

import java.util.Collection;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * overrides for {@link CommandSourceStack} so that the methods will run successfully client side
 */
public class ClientCommandSourceStack extends CommandSourceStack {
    public ClientCommandSourceStack(CommandSource source, Vec3 position, Vec2 rotation, PermissionSet permission, String plainTextName, Component displayName, Entity executing) {
        super(source, position, rotation, null, permission, plainTextName, displayName, null, executing);
    }

    /**
     * Sends a success message without attempting to get the server side list of admins
     */
    @SuppressWarnings("resource")
    @Override
    public void sendSuccess(Supplier<Component> message, boolean sendToAdmins) {
        Minecraft.getInstance().gui.hud.getChat().addClientSystemMessage(message.get());
    }

    /**
     * {@return the list of teams from the client side}
     */
    @SuppressWarnings("resource")
    @Override
    public Collection<String> getAllTeams() {
        return Minecraft.getInstance().level.getScoreboard().getTeamNames();
    }

    /**
     * {@return the list of online player names from the client side}
     */
    @Override
    public Collection<String> getOnlinePlayerNames() {
        return Minecraft.getInstance().getConnection().getOnlinePlayers().stream().map((player) -> player.getProfile().name()).collect(Collectors.toList());
    }

    /**
     * {@return a set of {@link ResourceKey} for levels from the client side}
     */
    @Override
    public Set<ResourceKey<Level>> levels() {
        return Minecraft.getInstance().getConnection().levels();
    }

    /**
     * {@return the {@link RegistryAccess} from the client side}
     */
    @Override
    public RegistryAccess registryAccess() {
        return Minecraft.getInstance().getConnection().registryAccess();
    }

    /**
     * {@return the scoreboard from the client side}
     */
    @SuppressWarnings("resource")
    @Override
    public Scoreboard getScoreboard() {
        return Minecraft.getInstance().level.getScoreboard();
    }

    /**
     * {@return the advancement from the id from the client side where the advancement needs to be visible to the player}
     */
    @Override
    public AdvancementHolder getAdvancement(Identifier id) {
        return Minecraft.getInstance().getConnection().getAdvancements().get(id);
    }

    /**
     * {@return the level from the client side}
     */
    @SuppressWarnings("resource")
    @Override
    public Level getUnsidedLevel() {
        return Minecraft.getInstance().level;
    }

    /**
     * @throws UnsupportedOperationException
     *             because the server isn't available on the client
     */
    @Override
    public MinecraftServer getServer() {
        throw new UnsupportedOperationException("Attempted to get server in client command");
    }

    /**
     * @throws UnsupportedOperationException
     *             because the server side level isn't available on the client side
     */
    @Override
    public ServerLevel getLevel() {
        throw new UnsupportedOperationException("Attempted to get server level in client command");
    }
}
