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
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientCommandSource extends CommandSourceStack
{

    public ClientCommandSource(CommandSource p_i49552_1_, Vec3 p_i49552_2_, Vec2 p_i49552_3_, int p_i49552_5_, String p_i49552_6_, Component p_i49552_7_,
            Entity p_i49552_9_)
    {
        super(p_i49552_1_, p_i49552_2_, p_i49552_3_, null, p_i49552_5_, p_i49552_6_, p_i49552_7_, null, p_i49552_9_);
    }

    @Override
    public void sendSuccess(Component p_197030_1_, boolean p_197030_2_)
    {
        Minecraft.getInstance().player.sendMessage(p_197030_1_, Util.NIL_UUID);
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

}
