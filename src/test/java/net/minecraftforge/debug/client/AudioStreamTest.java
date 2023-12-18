/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.AudioStreamManager;
import net.minecraft.client.audio.IAudioStream;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.LocatableSound;
import net.minecraft.client.audio.Sound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.lwjgl.BufferUtils;

import javax.annotation.Nonnull;
import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

/**
 * Tests support for custom {@link IAudioStream}s ({@link ISound#getStream(AudioStreamManager, Sound, boolean)}.
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
                Minecraft mc = Minecraft.getInstance();
                mc.getSoundManager().play(new SineSound(mc.player.position()));
            }
        }
    }

    public static class SineSound extends LocatableSound
    {
        protected SineSound(Vector3d position)
        {
            super(new ResourceLocation(MOD_ID, "sine_wave"), SoundCategory.BLOCKS);
            x = position.x;
            y = position.y;
            z = position.z;
        }

        @Nonnull
        @Override
        public CompletableFuture<IAudioStream> getStream(@Nonnull AudioStreamManager soundBuffers, @Nonnull Sound sound, boolean looping)
        {
            return CompletableFuture.completedFuture(new SineStream());
        }
    }

    public static class SineStream implements IAudioStream
    {
        private static final AudioFormat FORMAT = new AudioFormat(44100, 8, 1, false, false);
        private static final double DT = 2 * Math.PI * 220 / 44100;

        private static double value = 0;

        @Nonnull
        @Override
        public AudioFormat getFormat()
        {
            return FORMAT;
        }

        @Nonnull
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