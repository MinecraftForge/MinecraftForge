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

package net.minecraftforge.common.util;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class LogicalSidedProvider<T>
{
    public static final LogicalSidedProvider<BlockableEventLoop<? super TickTask>> WORKQUEUE = new LogicalSidedProvider<>(Supplier::get, Supplier::get);
    public static final LogicalSidedProvider<Optional<Level>> CLIENTWORLD = new LogicalSidedProvider<>((c)-> Optional.of(c.get().level), (s)->Optional.empty());

    private static Supplier<Minecraft> client;
    private static Supplier<MinecraftServer> server;

    // INTERNAL, DO  NOT CALL
    public static void setClient(Supplier<Minecraft> client)
    {
        LogicalSidedProvider.client = client;
    }
    public static void setServer(Supplier<MinecraftServer> server)
    {
        LogicalSidedProvider.server = server;
    }

    private LogicalSidedProvider(Function<Supplier<Minecraft>, T> clientSide, Function<Supplier<MinecraftServer>, T> serverSide)
    {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
    }

    private final Function<Supplier<Minecraft>, T> clientSide;
    private final Function<Supplier<MinecraftServer>, T> serverSide;

    public T get(final LogicalSide side) {
        return side==LogicalSide.CLIENT ? clientSide.apply(client) : serverSide.apply(server);
    }
}
