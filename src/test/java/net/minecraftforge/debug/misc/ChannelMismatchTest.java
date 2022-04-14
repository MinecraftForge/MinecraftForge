/*
 * Minecraft Forge
 * Copyright (c) 2016-2022.
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

package net.minecraftforge.debug.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ModMismatchDisconnectedScreen;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * This class registers a {@link SimpleChannel} with a different protocol version on the client and the server to cause a mod channel mismatch.
 * With this test mod enabled, a {@link ModMismatchDisconnectedScreen} should appear when trying to join a server, displaying information
 * about the mismatched channel.
 * In case of a mismatch, the two displayed mod versions will be the same due to not being able to specify a different client and server mod version of this test mod.
 * This test mod is disabled by default to ensure that users can join test servers without needing to specifically disable this test mod.
 */
@Mod(ChannelMismatchTest.MOD_ID)
public class ChannelMismatchTest
{
    public static final String MOD_ID = "channel_mismatch";

    private static final boolean ENABLED = false;
    //Disable this to cause a missing mod to be logged instead of a mod mismatch. Disabling it will make the server send a S2CModMismatchData packet to the client, containing information about the missing clientside mod.
    private static final boolean REGISTER_FOR_CLIENT = true;

    private static final String PROTOCOL_VERSION = FMLEnvironment.dist == Dist.CLIENT ? "V1" : "V2";

    public ChannelMismatchTest()
    {
        if (ENABLED)
        {
            if (FMLEnvironment.dist == Dist.DEDICATED_SERVER || REGISTER_FOR_CLIENT)
                NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "channel"), () -> PROTOCOL_VERSION, p -> p.equals(PROTOCOL_VERSION), (p) -> p.equals(PROTOCOL_VERSION));
        }
    }
}
