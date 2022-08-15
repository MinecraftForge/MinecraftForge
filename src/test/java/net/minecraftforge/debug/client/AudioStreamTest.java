/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * Tests support for custom {@link AudioStream}s ({@link SoundInstance#getStream(SoundBufferLibrary, Sound, boolean)}.
 *
 * When the message "sine wave" is sent in chat, this should play a sine wave of 220Hz at the player's current position.
 */
@Mod(AudioStreamTest.MOD_ID)
public class AudioStreamTest
{
    private static final boolean ENABLED = true;
    static final String MOD_ID = "audio_stream_test";

    public AudioStreamTest()
    {
        if (ENABLED && FMLLoader.getDist().isClient())
        {
            MinecraftForge.EVENT_BUS.addListener(ClientHandler::onClientChatEvent);
        }
    }

    public static class ClientHandler
    {
        public static void onClientChatEvent(ClientChatEvent event)
        {
            if (event.getMessage().equalsIgnoreCase("sine wave"))
            {
                event.setCanceled(true);
                var mc = Minecraft.getInstance();
                mc.getSoundManager().play(new SineSound(mc.player.position()));
            }
        }
    }

    public static class SineSound extends AbstractSoundInstance
    {
        protected SineSound(Vec3 position)
        {
            super(new ResourceLocation(MOD_ID, "sine_wave"), SoundSource.BLOCKS, SoundInstance.createUnseededRandom());
            x = position.x;
            y = position.y;
            z = position.z;
        }

        @NotNull
        @Override
        public CompletableFuture<AudioStream> getStream(@NotNull SoundBufferLibrary soundBuffers, @NotNull Sound sound, boolean looping)
        {
            return CompletableFuture.completedFuture(new SineStream());
        }
    }

    public static class SineStream implements AudioStream
    {
        private static final AudioFormat FORMAT = new AudioFormat(44100, 8, 1, false, false);
        private static final double DT = 2 * Math.PI * 220 / 44100;

        private static double value = 0;

        @NotNull
        @Override
        public AudioFormat getFormat()
        {
            return FORMAT;
        }

        @NotNull
        @Override
        public ByteBuffer read(int capacity)
        {
            ByteBuffer buffer = BufferUtils.createByteBuffer(capacity);
            for(int i = 0; i < capacity; i++)
            {
                buffer.put(i, (byte) (Math.sin(value) * 127));
                value = (value + DT) % Math.PI;
            }
            return buffer;
        }

        @Override
        public void close()
        {
        }
    }
}
