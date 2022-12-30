/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.misc;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ModMismatchDisconnectedScreen;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * This test mod provides a way to register a {@link SimpleChannel} with a different protocol version on the client and the server to cause a mod channel mismatch.
 * Additionally, this test mod can register a registry object for the dedicated server only, causing a registry mismatch. Registering a registry object for the client only is not supported, since that would not cause a registry mismatch.
 * With this test mod and at least one of its features enabled, a {@link ModMismatchDisconnectedScreen} should appear when trying to join a test server,
 * displaying detailed information about why the handshake failed.
 * In case of a mismatch, the two displayed mod versions will be the same due to not being able to specify a different client and server mod version of this test mod.
 * This test mod is disabled by default to ensure that users can join test servers without needing to specifically disable this test mod.
 */
@Mod(ModMismatchTest.MOD_ID)
public class ModMismatchTest
{
    public static final String MOD_ID = "mod_mismatch_test";

    private static final boolean ENABLED = false;
    // Enable these fields to register the channel for either the server, the client, or both.
    // If the channel is enabled for both dists, this test mod will be identified as mismatching between server and client.
    // If the channel is enabled for one dist only, this test mod will be identified as missing from the dist the channel hasn't been registered for.
    // Additionally, if the channel is missing for the client, a S2CModMismatchData packet will be sent to the client, containing all the information about the channel mismatch detected on the server.
    private static final boolean REGISTER_FOR_SERVER = true;
    private static final boolean REGISTER_FOR_CLIENT = true;
    // Enabling this field (and disabling the two above to not cause a channel mismatch) will cause a registry mismatch due to a server registry entry not being present on the client. Since this test mod is loaded on both dists, a mod mismatch will be displayed as the cause.
    private static final boolean REGISTER_REGISTRY_ENTRY = false;

    private static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MOD_ID);
    private static final String CHANNEL_PROTOCOL_VERSION = FMLEnvironment.dist == Dist.CLIENT ? "V1" : "V2";

    public ModMismatchTest()
    {
        if (ENABLED)
        {
            if ((FMLEnvironment.dist == Dist.DEDICATED_SERVER && REGISTER_FOR_SERVER) || (FMLEnvironment.dist == Dist.CLIENT && REGISTER_FOR_CLIENT))
            {
                NetworkRegistry.newSimpleChannel(new ResourceLocation(MOD_ID, "channel"), () -> CHANNEL_PROTOCOL_VERSION, p -> p.equals(CHANNEL_PROTOCOL_VERSION), (p) -> p.equals(CHANNEL_PROTOCOL_VERSION));
            }
            if (REGISTER_REGISTRY_ENTRY && FMLEnvironment.dist == Dist.DEDICATED_SERVER)
            {
                IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
                SOUND_EVENTS.register("mismatching_sound_event", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "server.connect.fail")));
                SOUND_EVENTS.register(eventBus);
            }
        }
    }
}
