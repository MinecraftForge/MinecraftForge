package net.minecraftforge.common.data;

import com.google.common.base.Preconditions;
import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import javax.annotation.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@SuppressWarnings("deprecation")
public abstract class SoundsProvider implements IDataProvider {
    private final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final Map<String, SoundEventBuilder> data = new TreeMap<>();
    private final DataGenerator gen;
    private final String modid;

    public SoundsProvider(DataGenerator gen, String modid) {
        this.gen = gen;
        this.modid = modid;
    }

    protected abstract void addSounds();

    @Override
    public void act(@Nonnull DirectoryCache cache) {
        data.clear();
        addSounds();
        if (!data.isEmpty()) {
            JsonObject jsonObject = new JsonObject();
            for (Map.Entry<String, SoundEventBuilder> entry : data.entrySet()) {
                jsonObject.add(entry.getKey(), entry.getValue().toJson());
            }
            try {
                IDataProvider.save(GSON, cache, jsonObject, gen.getOutputFolder().resolve("assets/" + modid + "/sounds.json"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public String getName() {
        return "Sounds";
    }

    protected void addSoundEvent(SoundEventBuilder soundEvent) {
        String path = soundEvent.getPath();
        if (data.containsKey(path)) {
            throw new RuntimeException("Sound event '" + path + "' has already been added.");
        }
        data.put(path, soundEvent);
    }


    public static class SoundEventBuilder{

        public static SoundEventBuilder create(SoundEvent soundEvent) {
            return new SoundEventBuilder(soundEvent);
        }

        private final String path;
        private boolean replace;
        private String subtitle;
        private Map<ResourceLocation, SoundBuilder> soundBuilders = new HashMap<>();

        public SoundEventBuilder(SoundEvent soundEvent){

            path = soundEvent.getRegistryName().getPath();

        }

        public String getPath() {
            return path;
        }

        public SoundEventBuilder replace() {
            this.replace = true;
            return this;
        }

        public SoundEventBuilder subtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public SoundEventBuilder addSounds(SoundBuilder... soundBuilders) {
            for (SoundBuilder soundBuilder : soundBuilders) {
                ResourceLocation location = soundBuilder.getFileLocation();
                if (this.soundBuilders.containsKey(location)) {
                    throw new RuntimeException("Sound '" + location + "' has already been added to this sound event.");
                }
                this.soundBuilders.put(location, soundBuilder);
            }
            return this;
        }

        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            if (replace) {
                jsonObject.addProperty("replace", replace);
            }
            if (subtitle != null) {
                jsonObject.addProperty("subtitle", subtitle);
            }
            if (!soundBuilders.isEmpty()) {
                JsonArray sounds = new JsonArray();
                for (SoundBuilder soundBuilder : soundBuilders.values()) {
                    sounds.add(soundBuilder.toJson());
                }
                jsonObject.add("sounds", sounds);
            }
            return jsonObject;
        }

    }

    public static class SoundBuilder{

        private final ResourceLocation fileLocation;
        private float volume = 1;
        private float pitch = 1;
        private int weight = 1;
        private boolean stream;
        private int attenuationDistance = 16;
        private boolean preload;
        private SoundType type = SoundType.SOUND;

        public SoundBuilder(ResourceLocation fileLocation){

            this.fileLocation = fileLocation;

        }

        public ResourceLocation getFileLocation() {
            return fileLocation;
        }

        public SoundBuilder volume(float volume) {
            if (volume < 0 || volume > 1) {
                throw new RuntimeException("Error volume for sound: '" + serializeLocation() + "' ,sound must be between 0.0 and 1.0 inclusive.");
            }
            this.volume = volume;
            return this;
        }

        public SoundBuilder pitch(float pitch) {
            this.pitch = pitch;
            return this;
        }

        public SoundBuilder weight(int weight) {
            if (weight < 1) {
                throw new RuntimeException("Error weight for sound: '" + serializeLocation() + "' ,weight must be at least 1.");
            }
            this.weight = weight;
            return this;
        }

        public SoundBuilder stream() {
            this.stream = true;
            return this;
        }

        public SoundBuilder attenuationDistance(int attenuationDistance) {
            if (attenuationDistance < 1) {
                throw new RuntimeException("Error attenuation distance for sound: '" + serializeLocation() + "' ,attenuation_distance must be at least 1.");
            }
            this.attenuationDistance = attenuationDistance;
            return this;
        }

        public SoundBuilder preload() {
            this.preload = true;
            return this;
        }

        public SoundBuilder type(SoundType type) {
            this.type = type;
            return this;
        }

        public JsonElement toJson() {
            JsonObject jsonObject = new JsonObject();
            boolean hasSettings = false;
            if (volume != 1) {
                jsonObject.addProperty("volume", volume);
                hasSettings = true;
            }
            if (pitch != 1) {
                jsonObject.addProperty("pitch", pitch);
                hasSettings = true;
            }
            if (weight != 1) {
                jsonObject.addProperty("weight", weight);
                hasSettings = true;
            }
            if (stream) {
                jsonObject.addProperty("stream", stream);
                hasSettings = true;
            }
            if (attenuationDistance != 16) {
                jsonObject.addProperty("attenuation_distance", attenuationDistance);
                hasSettings = true;
            }
            if (preload) {
                jsonObject.addProperty("preload", preload);
                hasSettings = true;
            }
            if (type != SoundType.SOUND) {
                jsonObject.addProperty("type", type.value);
                hasSettings = true;
            }
            if (hasSettings) {
                jsonObject.addProperty("name", serializeLocation());
                return jsonObject;
            }
            return new JsonPrimitive(serializeLocation());
        }

        private String serializeLocation() {
            if (fileLocation.getNamespace().equals("minecraft")) {
                return fileLocation.getPath();
            }
            return fileLocation.toString();
        }

        public enum SoundType {
            SOUND("sound"),
            EVENT("event");

            private final String value;

            SoundType(String value) {
                this.value = value;
            }
        }


    }

}

