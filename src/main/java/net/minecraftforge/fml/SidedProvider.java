/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import net.minecraftforge.fml.client.ClientHooks;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Function;
import java.util.function.Supplier;

public enum SidedProvider
{
    // All of these need to be careful not to directly dereference the client and server elements in their signatures
    DATAFIXER(
            c->c.get().getDataFixer(),
            s->s.get().getDataFixer(),
            ()-> { throw new UnsupportedOperationException(); }),
    SIDED_SETUP_EVENT(
            (Function<Supplier<Minecraft>, Function<ModContainer, Event>>)c-> mc->new FMLClientSetupEvent(c, mc),
            s-> mc->new FMLDedicatedServerSetupEvent(mc),
            ()-> { throw new UnsupportedOperationException(); }),
    STRIPCHARS(
            (Function<Supplier<Minecraft>, Function<String, String>>)c-> ClientHooks::stripSpecialChars,
            s-> str->str,
            ()-> str->str),
    @SuppressWarnings("Convert2MethodRef") // need to not be methodrefs to avoid classloading all of StartupQuery's data (supplier is coming from StartupQuery)
    STARTUPQUERY(
            c->StartupQuery.QueryWrapperClient.clientQuery(c),
            s->StartupQuery.QueryWrapperServer.dedicatedServerQuery(s),
            ()-> { throw new UnsupportedOperationException(); });

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
    private final Supplier<?> testSide;

    <T> SidedProvider(Function<Supplier<Minecraft>, T> clientSide, Function<Supplier<DedicatedServer>, T> serverSide, Supplier<T> testSide)
    {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
        this.testSide = testSide;
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
            return (T)this.testSide.get();
        }
    }
}
