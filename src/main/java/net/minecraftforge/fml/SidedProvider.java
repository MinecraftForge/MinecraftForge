/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml;

import net.minecraft.client.Minecraft;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.client.SplashProgress;
import net.minecraftforge.fml.common.event.FMLClientInitEvent;
import net.minecraftforge.fml.common.event.FMLServerInitEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Function;
import java.util.function.Supplier;

public enum SidedProvider
{
    DATAFIXER(c->c.get().getDataFixer(), s->s.get().getDataFixer()),
    SIDEDINIT((Function<Supplier<Minecraft>, Function<ModContainer, Event>>)c-> mc->new FMLClientInitEvent(c.get(), mc),
            (Function<Supplier<DedicatedServer>, Function<ModContainer, Event>>)s-> mc->new FMLServerInitEvent(s.get(), mc)),
    STRIPCHARS((Function<Supplier<Minecraft>, Function<String, String>>)c-> SplashProgress::stripSpecialChars,
            (Function<Supplier<DedicatedServer>, Function<String, String>>)s-> str->str),
    STARTUPQUERY(StartupQuery::clientQuery, StartupQuery::dedicatedServerQuery);

    private static Supplier<Minecraft> client;
    private static Supplier<DedicatedServer> server;

    public static void setClient(Supplier<Minecraft> client)
    {
        SidedProvider.client = client;
    }
    public static void setServer(Supplier<DedicatedServer> server)
    {
        SidedProvider.server = server;
    }

    private final Function<Supplier<Minecraft>, ?> clientSide;
    private final Function<Supplier<DedicatedServer>, ?> serverSide;


    SidedProvider(Function<Supplier<Minecraft>,?> clientSide, Function<Supplier<DedicatedServer>,?> serverSide)
    {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            return (T)this.clientSide.apply(client);
        }
        else if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            return (T)this.serverSide.apply(server);
        }
        else {
            throw new IllegalArgumentException("THREE SIDES? WUT?");
        }
    }
}