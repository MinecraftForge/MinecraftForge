/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common.data;

import com.google.gson.JsonObject;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Data provider for the {@code sounds.json} file, which identifies sound definitions
 * for the various sound events in Minecraft.
 */
public abstract class SoundDefinitionsProvider implements DataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final PackOutput output;
    private final String modId;
    private final ExistingFileHelper helper;

    private final Map<String, SoundDefinition> sounds = new LinkedHashMap<>();

    /**
     * Creates a new instance of this data provider.
     *
     * @param output The {@linkplain PackOutput} instance provided by the data generator.
     * @param modId The mod ID of the current mod.
     * @param helper The existing file helper provided by the event you are initializing this provider in.
     */
    protected SoundDefinitionsProvider(final PackOutput output, final String modId, final ExistingFileHelper helper)
    {
        this.output = output;
        this.modId = modId;
        this.helper = helper;
    }

    /**
     * Registers the sound definitions that should be generated via one of the {@code add} methods.
     */
    public abstract void registerSounds();

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        this.sounds.clear();
        this.registerSounds();
        this.validate();
        if (!this.sounds.isEmpty())
        {
            return this.save(cache, this.output.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(this.modId).resolve("sounds.json"));
        }

        return CompletableFuture.allOf();
    }

    @Override
    public String getName()
    {
        return "Sound Definitions";
    }

    // Quick helpers
    /**
     * Creates a new {@link SoundDefinition}, which will host a set of
     * {@link SoundDefinition.Sound}s and the necessary parameters.
     */
    protected static SoundDefinition definition()
    {
        return SoundDefinition.definition();
    }

    /**
     * Creates a new sound with the given name and type.
     *
     * @param name The name of the sound to create.
     * @param type The type of sound to create.
     */
    protected static SoundDefinition.Sound sound(final ResourceLocation name, final SoundDefinition.SoundType type)
    {
        return SoundDefinition.Sound.sound(name, type);
    }

    /**
     * Creates a new sound with the given name and {@link SoundDefinition.SoundType#SOUND} as
     * sound type.
     *
     * @param name The name of the sound to create.
     */
    protected static SoundDefinition.Sound sound(final ResourceLocation name)
    {
        return sound(name, SoundDefinition.SoundType.SOUND);
    }

    /**
     * Creates a new sound with the given name and type.
     *
     * @param name The name of the sound to create.
     * @param type The type of sound to create.
     */
    protected static SoundDefinition.Sound sound(final String name, final SoundDefinition.SoundType type)
    {
        return sound(new ResourceLocation(name), type);
    }

    /**
     * Creates a new sound with the given name and {@link SoundDefinition.SoundType#SOUND} as
     * sound type.
     *
     * @param name The name of the sound to create.
     */
    protected static SoundDefinition.Sound sound(final String name)
    {
        return sound(new ResourceLocation(name));
    }

    // Addition methods
    /**
     * Adds the entry name associated with the supplied {@link SoundEvent} with the given
     * {@link SoundDefinition} to the list.
     *
     * <p>This method should be preferred when dealing with a {@code RegistryObject} or
     * {@code RegistryDelegate}.</p>
     *
     * @param soundEvent A {@code Supplier} for the given {@link SoundEvent}.
     * @param definition A {@link SoundDefinition} that defines the given sound.
     */
    protected void add(final Supplier<SoundEvent> soundEvent, final SoundDefinition definition)
    {
        this.add(soundEvent.get(), definition);
    }

    /**
     * Adds the entry name associated with the given {@link SoundEvent} with the
     * {@link SoundDefinition} to the list.
     *
     * <p>This method should be preferred when a {@code SoundEvent} is already
     * available in the method context. If you already have a {@code Supplier} for
     * it, refer to {@link #add(Supplier, SoundDefinition)}.</p>
     *
     * @param soundEvent A {@link SoundEvent}.
     * @param definition The {@link SoundDefinition} that defines the given event.
     */
    protected void add(final SoundEvent soundEvent, final SoundDefinition definition)
    {
        this.add(soundEvent.getLocation(), definition);
    }

    /**
     * Adds the {@link SoundEvent} referenced by the given {@link ResourceLocation} with the
     * {@link SoundDefinition} to the list.
     *
     * @param soundEvent The {@link ResourceLocation} that identifies the event.
     * @param definition The {@link SoundDefinition} that defines the given event.
     */
    protected void add(final ResourceLocation soundEvent, final SoundDefinition definition)
    {
        this.addSounds(soundEvent.getPath(), definition);
    }

    /**
     * Adds the {@link SoundEvent} with the specified name along with its {@link SoundDefinition}
     * to the list.
     *
     * <p>The given sound event must NOT contain the namespace the name is a part of, since
     * the sound definition specification doesn't allow sounds to be defined outside the
     * namespace they're in. For this reason, any namespace will automatically be stripped
     * from the name.</p>
     *
     * @param soundEvent The name of the {@link SoundEvent}.
     * @param definition The {@link SoundDefinition} that defines the given event.
     */
    protected void add(final String soundEvent, final SoundDefinition definition)
    {
        this.add(new ResourceLocation(soundEvent), definition);
    }

    private void addSounds(final String soundEvent, final SoundDefinition definition)
    {
        if (this.sounds.put(soundEvent, definition) != null)
        {
            throw new IllegalStateException("Sound event '" + this.modId + ":" + soundEvent + "' already exists");
        }
    }

    // Internal handling stuff
    private void validate()
    {
        final List<String> notValid = this.sounds.entrySet().stream()
                .filter(it -> !this.validate(it.getKey(), it.getValue()))
                .map(Map.Entry::getKey)
                .map(it -> this.modId + ":" + it)
                .toList();
        if (!notValid.isEmpty())
        {
            throw new IllegalStateException("Found invalid sound events: " + notValid);
        }
    }

    private boolean validate(final String name, final SoundDefinition def)
    {
        return def.soundList().stream().allMatch(it -> this.validate(name,it));
    }

    private boolean validate(final String name, final SoundDefinition.Sound sound)
    {
        switch (sound.type())
        {
            case SOUND: return this.validateSound(name, sound.name());
            case EVENT: return this.validateEvent(name, sound.name());
        }
        // Differently from all the other errors, this is not a 'missing sound' but rather something completely different
        // that has broken the invariants of this sound definition's provider. In fact, a sound may only be either of
        // SOUND or EVENT type. Any other values is somebody messing with the internals, reflectively adding something
        // to an enum or passing `null` to a parameter annotated with `@NotNull`.
        throw new IllegalArgumentException("The given sound '" + sound.name() + "' does not have a valid type: expected either SOUND or EVENT, but found " + sound.type());
    }

    private boolean validateSound(final String soundName, final ResourceLocation name)
    {
        final boolean valid = this.helper.exists(name, PackType.CLIENT_RESOURCES, ".ogg", "sounds");
        if (!valid)
        {
            final String path = name.getNamespace() + ":sounds/" + name.getPath() + ".ogg";
            LOGGER.warn("Unable to find corresponding OGG file '{}' for sound event '{}'", path, soundName);
        }
        return valid;
    }

    private boolean validateEvent(final String soundName, final ResourceLocation name)
    {
        final boolean valid = this.sounds.containsKey(soundName) || ForgeRegistries.SOUND_EVENTS.containsKey(name);
        if (!valid)
        {
            LOGGER.warn("Unable to find event '{}' referenced from '{}'", name, soundName);
        }
        return valid;
    }

    private CompletableFuture<?> save(final CachedOutput cache, final Path targetFile)
    {
        return DataProvider.saveStable(cache, this.mapToJson(this.sounds), targetFile);
    }

    private JsonObject mapToJson(final Map<String, SoundDefinition> map)
    {
        final JsonObject obj = new JsonObject();
        // namespaces are ignored when serializing
        map.forEach((k, v) -> obj.add(k, v.serialize()));
        return obj;
    }
}
