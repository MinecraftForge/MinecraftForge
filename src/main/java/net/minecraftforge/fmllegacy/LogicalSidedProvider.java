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

package net.minecraftforge.fmllegacy;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public enum LogicalSidedProvider
{
    WORKQUEUE((c)->c.get(), (s)->s.get()),
    INSTANCE((c)->c.get(), (s)->s.get()),
    CLIENTWORLD((c)-> Optional.<Level>of(c.get().level), (s)->Optional.<Level>empty())
    ;
    private static Supplier<Minecraft> client;
    private static Supplier<MinecraftServer> server;

    LogicalSidedProvider(Function<Supplier<Minecraft>, ?> clientSide, Function<Supplier<MinecraftServer>, ?> serverSide)
    {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
    }

    public static void setClient(Supplier<Minecraft> client)
    {
        LogicalSidedProvider.client = client;
    }
    public static void setServer(Supplier<MinecraftServer> server)
    {
        LogicalSidedProvider.server = server;
    }


    private final Function<Supplier<Minecraft>, ?> clientSide;
    private final Function<Supplier<MinecraftServer>, ?> serverSide;

    @SuppressWarnings("unchecked")
    public <T> T get(final LogicalSide side) {
        return (T)(side==LogicalSide.CLIENT ? clientSide.apply(client) : serverSide.apply(server));
    }
}
