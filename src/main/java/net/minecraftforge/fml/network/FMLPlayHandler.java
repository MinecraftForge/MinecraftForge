/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.network;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class FMLPlayHandler
{
    public static final SimpleChannel channel = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation("fml", "play"))
            .clientAcceptedVersions(a -> true)
            .serverAcceptedVersions(a -> true)
            .networkProtocolVersion(() -> NetworkHooks.NETVERSION)
            .simpleChannel();
    static
    {
        channel.registerMessage(0, FMLPlayMessages.SpawnEntity.class, FMLPlayMessages.SpawnEntity::encode, FMLPlayMessages.SpawnEntity::decode, FMLPlayMessages.SpawnEntity::handle);
    }
}
