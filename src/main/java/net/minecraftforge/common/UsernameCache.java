/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.google.common.base.Charsets;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.jetbrains.annotations.Nullable;

/**
 * Caches player's last known usernames
 * <p>
 * Modders should use {@link #getLastKnownUsername(UUID)} to determine a players
 * last known username.<br>
 * For convenience, {@link #getMap()} is provided to get an immutable copy of
 * the caches underlying map.
 */
public final class UsernameCache {

    private static Map<UUID, String> map = new HashMap<>();

    private static final Path saveFile = FMLLoader.getGamePath().resolve("usernamecache.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Logger LOGGER = LogManager.getLogger(UsernameCache.class);
    private static final Marker USRCACHE = MarkerManager.getMarker("USERNAMECACHE");

    private UsernameCache() {}

    /**
     * Set a player's current usernamee
     *
     * @param uuid
     *            the player's {@link java.util.UUID UUID}
     * @param username
     *            the player's username
     */
    protected static void setUsername(UUID uuid, String username)
    {
        Objects.requireNonNull(uuid);
        Objects.requireNonNull(username);

        if (username.equals(map.get(uuid))) return;

        map.put(uuid, username);
        save();
    }

    /**
     * Remove a player's username from the cache
     *
     * @param uuid
     *            the player's {@link java.util.UUID UUID}
     * @return if the cache contained the user
     */
    protected static boolean removeUsername(UUID uuid)
    {
        Objects.requireNonNull(uuid);

        if (map.remove(uuid) != null)
        {
            save();
            return true;
        }

        return false;
    }

    /**
     * Get the player's last known username
     * <p>
     * <b>May be <code>null</code></b>
     *
     * @param uuid
     *            the player's {@link java.util.UUID UUID}
     * @return the player's last known username, or <code>null</code> if the
     *         cache doesn't have a record of the last username
     */
    @Nullable
    public static String getLastKnownUsername(UUID uuid)
    {
        Objects.requireNonNull(uuid);
        return map.get(uuid);
    }

    /**
     * Check if the cache contains the given player's username
     *
     * @param uuid
     *            the player's {@link java.util.UUID UUID}
     * @return if the cache contains a username for the given player
     */
    public static boolean containsUUID(UUID uuid)
    {
        Objects.requireNonNull(uuid);
        return map.containsKey(uuid);
    }

    /**
     * Get an immutable copy of the cache's underlying map
     *
     * @return the map
     */
    public static Map<UUID, String> getMap()
    {
        return ImmutableMap.copyOf(map);
    }

    /**
     * Save the cache to file
     */
    protected static void save()
    {
        new SaveThread(gson.toJson(map)).start();
    }

    /**
     * Load the cache from file
     */
    protected static void load()
    {
        if (!Files.exists(saveFile)) return;

        try (final BufferedReader reader = Files.newBufferedReader(saveFile, Charsets.UTF_8))
        {
            @SuppressWarnings("serial")
            Type type = new TypeToken<Map<UUID, String>>(){}.getType();
            map = gson.fromJson(reader, type);
        }
        catch (JsonSyntaxException | IOException e)
        {
            LOGGER.error(USRCACHE,"Could not parse username cache file as valid json, deleting file {}", saveFile, e);
            try
            {
                Files.delete(saveFile);
            }
            catch (IOException e1)
            {
                LOGGER.error(USRCACHE,"Could not delete file {}", saveFile.toString());
            }
        }
        finally
        {
            // Can sometimes occur when the json file is malformed
            if (map == null)
            {
                map = new HashMap<>();
            }
        }
    }

    /**
     * Used for saving the {@link com.google.gson.Gson#toJson(Object) Gson}
     * representation of the cache to disk
     */
    private static class SaveThread extends Thread {

        /** The data that will be saved to disk */
        private final String data;

        public SaveThread(String data)
        {
            this.data = data;
        }

        @Override
        public void run()
        {
            try
            {
                // Make sure we don't save when another thread is still saving
                synchronized (saveFile)
                {
                    Files.write(saveFile, data.getBytes(StandardCharsets.UTF_8));
                }
            }
            catch (IOException e)
            {
                LOGGER.error(USRCACHE, "Failed to save username cache to file!", e);
            }
        }
    }
}
