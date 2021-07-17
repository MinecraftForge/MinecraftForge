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

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ClientCommandSource extends CommandSource
{

    public ClientCommandSource(ICommandSource p_i49552_1_, Vector3d p_i49552_2_, Vector2f p_i49552_3_, int p_i49552_5_,
            String p_i49552_6_, ITextComponent p_i49552_7_, Entity p_i49552_9_)
    {
        super(p_i49552_1_, p_i49552_2_, p_i49552_3_, null, p_i49552_5_, p_i49552_6_, p_i49552_7_, null, p_i49552_9_);
    }

    @Override
    public void sendSuccess(ITextComponent p_197030_1_, boolean p_197030_2_)
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
    public Set<RegistryKey<World>> levels()
    {
        return Minecraft.getInstance().getConnection().levels();
    }

    @Override
    public DynamicRegistries registryAccess()
    {
        return Minecraft.getInstance().getConnection().registryAccess();
    }

}
