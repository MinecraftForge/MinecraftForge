/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.common;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import net.minecraftforge.fml.relauncher.FMLInjectionData;

/**
 * Caches player's last known usernames
 * <p>
 * Modders should use {@link #getLastKnownUsername(UUID)} to determine a players
 * last known username.<br>
 * For convenience, {@link #getMap()} is provided to get an immutable copy of
 * the caches underlying map.
 */
public final class UsernameCache {

    private static Map<UUID, String> map = Maps.newHashMap();

    private static final Charset charset = StandardCharsets.UTF_8;

    private static final File saveFile = new File( /* The minecraft dir */(File) FMLInjectionData.data()[6], "usernamecache.json");
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Logger log = LogManager.getLogger(ForgeVersion.MOD_ID + ".UsernameCache");

    private UsernameCache() {}

    /**
     * Set a player's current username
     *
     * @param uuid
     *            the player's {@link java.util.UUID UUID}
     * @param username
     *            the player's username
     */
    protected static void setUsername(UUID uuid, String username)
    {
        checkNotNull(uuid);
        checkNotNull(username);

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
        checkNotNull(uuid);

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
        checkNotNull(uuid);
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
        checkNotNull(uuid);
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
        if (!saveFile.exists()) return;

        try
        {

            String json = Files.toString(saveFile, charset);
            Type type = new TypeToken<Map<UUID, String>>() { private static final long serialVersionUID = 1L; }.getType();

            map = gson.fromJson(json, type);
        }
        catch (JsonSyntaxException e)
        {
            log.error("Could not parse username cache file as valid json, deleting file", e);
            saveFile.delete();
        }
        catch (IOException e)
        {
            log.error("Failed to read username cache file from disk, deleting file", e);
            saveFile.delete();
        }
        finally
        {
            // Can sometimes occur when the json file is malformed
            if (map == null)
            {
                map = Maps.newHashMap();
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
                    Files.write(data, saveFile, charset);
                }
            }
            catch (IOException e)
            {
                log.error("Failed to save username cache to file!", e);
            }
        }
    }
}