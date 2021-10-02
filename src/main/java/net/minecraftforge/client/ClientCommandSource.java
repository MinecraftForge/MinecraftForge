/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.client;

import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientCommandSource extends CommandSourceStack
{

    public ClientCommandSource(CommandSource source, Vec3 position, Vec2 rotation, int permission, String plainTextName, Component displayName,
            Entity executing)
    {
        super(source, position, rotation, null, permission, plainTextName, displayName, null, executing);
    }

    @Override
    public void sendSuccess(Component message, boolean sendToAdmins)
    {
        Minecraft.getInstance().player.sendMessage(message, Util.NIL_UUID);
    }

    @Override
    public Collection<String> getAllTeams()
    {
        return Minecraft.getInstance().level.getScoreboard().getTeamNames();
    }

    @Override
    public Collection<String> getOnlinePlayerNames()
    {
        return Minecraft.getInstance().getConnection().getOnlinePlayers().stream().map((player) -> player.getProfile().getName()).collect(Collectors.toList());
    }

    @Override
    public Stream<ResourceLocation> getRecipeNames()
    {
        return Minecraft.getInstance().getConnection().getRecipeManager().getRecipeIds();
    }

    @Override
    public Set<ResourceKey<Level>> levels()
    {
        return Minecraft.getInstance().getConnection().levels();
    }

    @Override
    public RegistryAccess registryAccess()
    {
        return Minecraft.getInstance().getConnection().registryAccess();
    }

    @Override
    public Scoreboard getScoreboard()
    {
        return Minecraft.getInstance().level.getScoreboard();
    }

    @Override
    public Advancement getAdvancement(ResourceLocation id)
    {
        return Minecraft.getInstance().getConnection().getAdvancements().getAdvancements().get(id);
    }

    @Override
    public RecipeManager getRecipeManager()
    {
        return Minecraft.getInstance().getConnection().getRecipeManager();
    }

    @Override
    public Level getUnsidedLevel()
    {
        return Minecraft.getInstance().level;
    }

    @Override
    public MinecraftServer getServer()
    {
        throw new UnsupportedOperationException("Attempted to get server in client command");
    }

    @Override
    public ServerLevel getLevel()
    {
        throw new UnsupportedOperationException("Attempted to get server level in client command");
    }

}
