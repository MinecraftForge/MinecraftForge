/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.fml.relauncher.libraries;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

/**
 * This is different from the standard maven snapshot metadata.
 * Because none of that data is exposed to us as a user of gradle/maven/whatever.
 * So we JUST use the timestamp.
 *
 * {
 *   "latest": "yyyyMMdd.hhmmss",
 *   "versions": [
 *     {
 *       "md5": "md5 in hex lowercase",
 *       "timestamp": "yyyyMMdd.hhmmss"
 *   ]
 * }
 *
 */
public class SnapshotJson implements Comparable<SnapshotJson>
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static final DateFormat TIMESTAMP = new SimpleDateFormat("yyyyMMdd.hhmmss");
    public static final String META_JSON_FILE = "maven-metadata.json";
    private static final Gson GSON = new GsonBuilder().create();
    private static final Comparator<Entry> SORTER = (o1, o2) -> o2.timestamp.compareTo(o1.timestamp);

    public static SnapshotJson create(File target)
    {
        if (!target.exists())
            return new SnapshotJson();

        try
        {
            String json = Files.asCharSource(target, StandardCharsets.UTF_8).read();
            SnapshotJson obj = GSON.fromJson(json, SnapshotJson.class);
            obj.updateLatest();
            return obj;
        }
        catch (JsonSyntaxException jse)
        {
            LOGGER.info("Failed to parse snapshot json file " + target + ".", jse);
        }
        catch (IOException ioe)
        {
            LOGGER.info("Failed to read snapshot json file " + target + ".", ioe);
        }

        return new SnapshotJson();
    }

    private String latest;
    private List<Entry> versions;

    public String getLatest()
    {
        return this.latest;
    }

    public void add(Entry data)
    {
        if (versions == null)
            versions = new ArrayList<>();
        versions.add(data);
        updateLatest();
    }

    public void merge(SnapshotJson o)
    {
        if (o.versions != null)
        {
            if (versions == null)
                versions = new ArrayList<>(o.versions);
            else
                o.versions.stream().filter(e -> versions.stream().anyMatch(e2 -> e.timestamp.equals(e2.timestamp))).forEach(e -> versions.add(e));
            updateLatest();
        }
    }

    public boolean remove(String timestamp)
    {
        if (versions == null)
            return false;
        if (versions.removeIf(e -> e.timestamp.equals(timestamp)))
            updateLatest();
        return false;
    }

    public String updateLatest()
    {
        if (versions == null)
        {
            latest = null;
            return null;
        }
        Collections.sort(versions, SORTER);
        return latest = versions.isEmpty() ? null : versions.get(0).timestamp;
    }

    public void write(File target) throws IOException
    {
        Files.write(GSON.toJson(this), target, StandardCharsets.UTF_8);
    }

    @Override
    public int compareTo(SnapshotJson o)
    {
        return o == null ? 1 : o.latest == null ? latest == null ? 0 : 1 : latest == null ? -1 : o.latest.compareTo(latest);
    }

    public static class Entry
    {
        private String timestamp;
        private String md5;

        public Entry(String timestamp, String md5)
        {
            this.timestamp = timestamp;
            this.md5 = md5;
        }

        public String getTimestamp()
        {
            return this.timestamp;
        }

        public String getMd5()
        {
            return this.md5;
        }
    }
}
