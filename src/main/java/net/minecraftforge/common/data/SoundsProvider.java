package net.minecraftforge.common.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@SuppressWarnings("deprecation")
public abstract class SoundsProvider implements IDataProvider {
    private final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, Sounds> data = new TreeMap<>();
    private final DataGenerator gen;
    private final String modid;

    public SoundsProvider(DataGenerator gen, String modid) {
        this.gen = gen;
        this.modid = modid;
    }

    protected abstract void addSounds();

    @Override
    public void act(DirectoryCache cache) throws IOException {
        addSounds();
        if (!data.isEmpty())
            save(cache, data, this.gen.getOutputFolder().resolve("assets/" + modid + "/sounds.json"));
    }

    @Override
    public String getName() {
        return "Sounds.json";
    }

    private void save(DirectoryCache cache, Object object, Path target) throws IOException {
        String data = GSON.toJson(object);
        System.out.println(data);
        data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data); // Escape unicode after the fact so that it's not double escaped by GSON
        String hash = IDataProvider.HASH_FUNCTION.hashUnencodedChars(data).toString();
        if (!Objects.equals(cache.getPreviousHash(target), hash) || !Files.exists(target)) {
            Files.createDirectories(target.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(target)) {
                bufferedwriter.write(data);
            }
        }

        cache.recordHash(target, hash);
    }

    public void addSound(String name ,String subtitle, SoundsFile... sounds) {
        Sounds sound = new Sounds();
        sound.setSubtitle(subtitle);
        sound.setSoundsFile(sounds);
        add(name, sound);
    }


    public void add(String key, Sounds value) {
        if (data.put(key, value) != null)
            throw new IllegalStateException("Duplicate translation key " + key);
    }

    public class Sounds{

        private SoundsFile[] sounds;
        private String subtitle;

        public Sounds(){


        }

        public void setSoundsFile(SoundsFile... soundsFile) {
            this.sounds = soundsFile;
        }

        public void setSubtitle(String subtitle) {
            this.subtitle = subtitle;
        }

    }

    public static class SoundsFile{

        public String name;
        public Float volume;
        public Float pitch;
        public Integer weight;
        public Boolean stream;
        public Integer attenuation_distance;
        public Boolean preload;

        public SoundsFile(){}

        public SoundsFile setName(String name){

            this.name = name;

            return this;

        }

        public SoundsFile setVolume(float volume){

            this.volume = volume;

            return this;

        }

        public SoundsFile setPitch(float pitch){

            this.pitch = pitch;

            return this;

        }

        public SoundsFile setWeight(int weight){

            this.weight = weight;

            return this;

        }

        public SoundsFile setStream(boolean stream){

            this.stream = stream;

            return this;

        }

        public SoundsFile setAttenuationDistance(int attenuationDistance){

            this.attenuation_distance = attenuationDistance;

            return this;

        }

        public SoundsFile setPreload(boolean preload){

            this.preload = preload;

            return this;

        }

    }

}

