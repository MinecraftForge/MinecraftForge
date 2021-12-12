package net.minecraftforge.debug.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.BufferUtils;

import javax.sound.sampled.AudioFormat;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;

@Mod(AudioStreamTest.MOD_ID)
public class AudioStreamTest
{
    private static final boolean ENABLED = true;
    static final String MOD_ID = "audio_stream_test";

    public AudioStreamTest()
    {
        if (ENABLED && FMLLoader.getDist().isClient())
        {
            MinecraftForge.EVENT_BUS.addListener(this::onClientChatEvent);
        }
    }

    public void onClientChatEvent(ClientChatEvent event)
    {
        if (event.getMessage().equalsIgnoreCase("sine wave"))
        {
            event.setCanceled(true);
            Minecraft.getInstance().getSoundManager().play(new SineSound());
        }
    }

    public static class SineSound extends AbstractSoundInstance
    {
        protected SineSound()
        {
            super(new ResourceLocation(MOD_ID, "sine_wave"), SoundSource.BLOCKS);
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
