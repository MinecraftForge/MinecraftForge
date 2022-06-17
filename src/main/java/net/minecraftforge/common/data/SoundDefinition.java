/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains all the data to completely define a sound event.
 */
public final class SoundDefinition
{
    /**
     * Identifies a specific sound that has to be played in a sound event, along with
     * all the necessary parameters.
     *
     * <p>If any of the optional parameters (i.e. the ones that aren't required to
     * obtain an instance of this class) are unset, their default values will be
     * used instead. The list of defaults is available in the text that follows:</p>
     *
     * <ul>
     *     <li>Volume: 1.0F</li>
     *     <li>Pitch: 1.0F</li>
     *     <li>Weight: 1</li>
     *     <li>Stream: false</li>
     *     <li>Attenuation Distance: 16</li>
     *     <li>Preload: false</li>
     * </ul>
     */
    public static final class Sound
    {
        private static final SoundType DEFAULT_TYPE = SoundType.SOUND;
        private static final float DEFAULT_VOLUME = 1.0F;
        private static final float DEFAULT_PITCH = 1.0F;
        private static final int DEFAULT_WEIGHT = 1;
        private static final boolean DEFAULT_STREAM = false;
        private static final int DEFAULT_ATTENUATION_DISTANCE = 16;
        private static final boolean DEFAULT_PRELOAD = false;

        private final ResourceLocation name;
        private final SoundType type;

        // Optional parameters, available in specs
        private float volume = DEFAULT_VOLUME;
        private float pitch = DEFAULT_PITCH;
        private int weight = DEFAULT_WEIGHT;
        private boolean stream = DEFAULT_STREAM;
        private int attenuationDistance = DEFAULT_ATTENUATION_DISTANCE;
        private boolean preload = DEFAULT_PRELOAD;

        private Sound(final ResourceLocation name, final SoundType type)
        {
            this.name = name;
            this.type = type;
        }

        /**
         * Creates a new sound with the given name and type.
         *
         * @param name The name of the sound to create.
         * @param type The type of sound to create.
         */
        public static Sound sound(final ResourceLocation name, final SoundType type)
        {
            return new Sound(name, type);
        }

        /**
         * Sets the volume of this specific sound.
         *
         * <p>The volume of a sound represents how <strong>loud</strong> the sound is when played.</p>
         *
         * @param volume The volume to set. It must be higher than 0.
         * @return This sound for chaining.
         */
        public Sound volume(final double volume)
        {
            return this.volume((float) volume);
        }

        /**
         * Sets the volume of this specific sound.
         *
         * <p>The volume of a sound represents how <strong>loud</strong> the sound is when played.</p>
         *
         * @param volume The volume to set. It must be higher than 0.
         * @return This sound for chaining.
         */
        public Sound volume(final float volume)
        {
            // Wiki specifies that volume cannot be higher than 1.0F, but I don't see any checks in vanilla nor in the
            // original specs (https://www.reddit.com/r/Minecraft/comments/1ont25/snapshot_13w42a_has_been_released/cctryvp/)
            // Set at own risk
            if (volume <= 0.0F) {
                throw new IllegalArgumentException("Volume must be positive for sound " + this.name + ", but instead got " + volume);
            }
            this.volume = volume;
            return this;
        }

        /**
         * Sets the pitch of this specific sound.
         *
         * <p>The pitch of a sound represents how <strong>high</strong> or <strong>low</strong> the sound is
         * when played.</p>
         *
         * @param pitch The pitch to set. It must be higher than 0.
         * @return This sound for chaining.
         */
        public Sound pitch(final double pitch)
        {
            return this.pitch((float) pitch);
        }

        /**
         * Sets the pitch of this specific sound.
         *
         * <p>The pitch of a sound represents how <strong>high</strong> or <strong>low</strong> the sound is
         * when played.</p>
         *
         * @param pitch The pitch to set. It must be higher than 0.
         * @return This sound for chaining.
         */
        public Sound pitch(final float pitch)
        {
            if (pitch <= 0.0F)
            {
                throw new IllegalArgumentException("Pitch must be positive for sound " + this.name + ", but instead got " + pitch);
            }
            this.pitch = pitch;
            return this;
        }

        /**
         * Sets the weight of this specific sound.
         *
         * <p>The weight represents how likely it is for this sound to be played when the respective
         * event is triggered. This value is ignored when there is only one sound per event.</p>
         *
         * @param weight The weight to set. It must be higher than 0.
         * @return This sound for chaining.
         */
        public Sound weight(final int weight)
        {
            if (weight <= 0)
            {
                throw new IllegalArgumentException("Weight has to be a positive number in sound " + this.name + ", but instead got " + weight);
            }
            this.weight = weight;
            return this;
        }

        /**
         * Sets this sound to a streamed sound.
         *
         * <p>In this context, streaming refers to reading the file on disk as needed instead of
         * loading the whole set in memory. This is useful in case of longer sounds, like records
         * and music (usually more than a minute).</p>
         *
         * <p>This is equivalent to a call to {@link #stream(boolean)} with a value of true.</p>
         *
         * @return This sound for chaining
         */
        public Sound stream()
        {
            return this.stream(true);
        }

        /**
         * Sets whether this sound should be streamed or not.
         *
         * <p>In this context, streaming refers to reading the file on disk as needed instead of
         * loading the whole set in memory. This is useful in case of longer sounds, like records
         * and music (usually more than a minute).</p>
         *
         * @param stream Whether the sound should be streamed or not.
         * @return This sound for chaining.
         */
        public Sound stream(final boolean stream)
        {
            this.stream = stream;
            return this;
        }

        /**
         * Sets the attenuation distance of the sound.
         *
         * <p>This represents how far this sound will be heard, in blocks. While the specs don't
         * require so, it is suggested to keep this value positive.</p>
         *
         * @param attenuationDistance The attenuation distance to set.
         * @return This sound for chaining.
         */
        public Sound attenuationDistance(final int attenuationDistance)
        {
            this.attenuationDistance = attenuationDistance;
            return this;
        }

        /**
         * Marks this sound as needing to be preloaded.
         *
         * <p>A preloaded sound identifies a sound that is loaded in memory as soon as the resource
         * pack is loaded, without having to wait for the sound to be ready to stream. It is suggested
         * to keep this to {@code false}, unless you are using it for a highly recurring sound (e.g.
         * underwater ambient sounds).</p>
         *
         * <p>This is equivalent to a call to {@link #preload(boolean)} with a value of true.</p>
         *
         * @return This sound for chaining.
         */
        public Sound preload()
        {
            return this.preload(true);
        }

        /**
         * Sets whether this sound should be preloaded or not.
         *
         * <p>A preloaded sound identifies a sound that is loaded in memory as soon as the resource
         * pack is loaded, without having to wait for the sound to be ready to stream. It is suggested
         * to keep this to {@code false}, unless you are using it for a highly recurring sound (e.g.
         * underwater ambient sounds).</p>
         *
         * @param preload Whether the sound should be preloaded or not.
         * @return This sound for chaining.
         */
        public Sound preload(final boolean preload)
        {
            this.preload = preload;
            return this;
        }

        ResourceLocation name()
        {
            return this.name;
        }

        SoundType type()
        {
            return this.type;
        }

        JsonElement serialize()
        {
            if (this.canBeInShortForm())
            {
                return new JsonPrimitive(this.stripMcPrefix(this.name));
            }

            final JsonObject object = new JsonObject();
            object.addProperty("name", this.stripMcPrefix(this.name));
            if (this.type != DEFAULT_TYPE) object.addProperty("type", this.type.jsonString);
            if (this.volume != DEFAULT_VOLUME) object.addProperty("volume", this.volume);
            if (this.pitch != DEFAULT_PITCH) object.addProperty("pitch", this.pitch);
            if (this.weight != DEFAULT_WEIGHT) object.addProperty("weight", this.weight);
            if (this.stream != DEFAULT_STREAM) object.addProperty("stream", this.stream);
            if (this.preload != DEFAULT_PRELOAD) object.addProperty("preload", this.preload);
            if (this.attenuationDistance != DEFAULT_ATTENUATION_DISTANCE) object.addProperty("attenuation_distance", this.attenuationDistance);
            return object;
        }

        private boolean canBeInShortForm()
        {
            return this.type == DEFAULT_TYPE &&
                    this.volume == DEFAULT_VOLUME &&
                    this.pitch == DEFAULT_PITCH &&
                    this.weight == DEFAULT_WEIGHT &&
                    this.stream == DEFAULT_STREAM &&
                    this.attenuationDistance == DEFAULT_ATTENUATION_DISTANCE &&
                    this.preload == DEFAULT_PRELOAD;
        }

        private String stripMcPrefix(final ResourceLocation name)
        {
            return "minecraft".equals(name.getNamespace()) ? name.getPath() : name.toString();
        }
    }

    /**
     * Represents the type of sound that the {@link Sound} object represents.
     */
    public enum SoundType
    {
        /**
         * Identifies a "normal" sound.
         *
         * <p>In a "normal" sound, the {@code name} is considered a file name, which
         * the game attempts to load from the currently loaded resource packs.</p>
         */
        SOUND("sound"),
        /**
         * Identifies a "redirect" sound.
         *
         * <p>In a "redirect" sound, the {@code name} of the sound is treated as the
         * name of another {@code SoundEvent}, which will be queried. Processing is
         * then deferred to that specific sound event, recursively as needed.</p>
         */
        EVENT("event");

        private final String jsonString;

        SoundType(final String jsonString)
        {
            this.jsonString = jsonString;
        }
    }

    private final List<Sound> sounds = new ArrayList<>();
    private boolean replace = false;
    private String subtitle = null;

    private SoundDefinition() {}

    /**
     * Creates a new {@link SoundDefinition}, which will host a set of
     * {@link Sound}s and the necessary parameters.
     */
    public static SoundDefinition definition()
    {
        return new SoundDefinition();
    }

    /**
     * Sets whether this definition should replace any other definition for the
     * same sound event previously applied, rather than overwriting it.
     *
     * @param replace Whether this definition replaces or not.
     * @return This definition for chaining.
     */
    public SoundDefinition replace(final boolean replace)
    {
        this.replace = replace;
        return this;
    }

    /**
     * Sets the language key for the subtitle that will be displayed whenever this
     * sound is being played.
     *
     * <p>The subtitle is optional and the game will skip displaying it if it
     * isn't present.</p>
     *
     * @param subtitle The subtitle to display, or null to disable.
     * @return This definition for chaining.
     */
    public SoundDefinition subtitle(@Nullable final String subtitle)
    {
        this.subtitle = subtitle;
        return this;
    }

    /**
     * Adds the given sound to this sound definition.
     *
     * @param sound The sound to add.
     * @return This definition for chaining.
     */
    public SoundDefinition with(final Sound sound)
    {
        this.sounds.add(sound);
        return this;
    }

    /**
     * Adds the given sounds to this sound definition.
     *
     * @param sounds The sounds to add.
     * @return This definition for chaining.
     */
    public SoundDefinition with(final Sound... sounds)
    {
        this.sounds.addAll(Arrays.asList(sounds));
        return this;
    }

    List<Sound> soundList()
    {
        return this.sounds;
    }

    JsonObject serialize()
    {
        if (this.sounds.isEmpty())
        {
            throw new IllegalStateException("Unable to serialize a sound definition that has no sounds!");
        }

        final JsonObject object = new JsonObject();
        if (this.replace) object.addProperty("replace", true);
        if (this.subtitle != null) object.addProperty("subtitle", this.subtitle);
        final JsonArray sounds = new JsonArray();
        this.sounds.stream().map(Sound::serialize).forEach(sounds::add);
        object.add("sounds", sounds);
        return object;
    }
}
