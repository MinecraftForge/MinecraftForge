/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

public class Repository
{
    private static final Logger LOGGER = LogManager.getLogger();

    static final Map<String, Repository> cache = new LinkedHashMap<>();

    public static Repository create(File root) throws IOException
    {
        return create(root, root.getCanonicalPath());
    }
    public static Repository create(File root, String name)
    {
        return cache.computeIfAbsent(name, f -> new Repository(root, name));
    }
    public static Repository replace(File root, String name)
    {
        return cache.put(name, new Repository(root, name));
    }
    public static Repository get(String name)
    {
        return cache.get(name);
    }
    public static Artifact resolveAll(Artifact artifact)
    {
        Artifact ret = null;
        for (Repository repo : cache.values())
        {
            Artifact tmp = repo.resolve(artifact);
            if (tmp == null)
                continue;
            if (!artifact.isSnapshot())
                return tmp; //If its a concrete version *assume* any resolved one in any repo will work. As they shouldn't release overriding versions.
            ret = ret == null || ret.compareTo(tmp) < 0 ? tmp : ret;
        }
        return ret;
    }


    private final String name;
    private final File root;

    protected Repository(File root) throws IOException
    {
        this(root, root.getCanonicalPath());
    }
    protected Repository(File root, String name)
    {
        this.root = root;
        this.name = name;
        if (name == null)
            throw new IllegalArgumentException("Invalid Repository Name, for " + root);
    }

    @Override
    public int hashCode()
    {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return o instanceof Repository && ((Repository)o).name.equals(name);
    }

    public Artifact resolve(Artifact artifact)
    {
        if (!artifact.isSnapshot())
            return getFile(artifact.getPath()).exists() ? artifact : null;

        File meta = getFile(artifact.getFolder() + File.separatorChar + SnapshotJson.META_JSON_FILE);
        if (!meta.exists())
            return null;

        SnapshotJson json = SnapshotJson.create(getFile(artifact.getFolder() + File.separatorChar + SnapshotJson.META_JSON_FILE));
        if (json.getLatest() == null)
            return null;

        Artifact ret = new Artifact(artifact, this, json.getLatest());
        while (json.getLatest() != null && !getFile(ret.getPath()).exists())
        {
            if (!json.remove(json.getLatest()))
                throw new IllegalStateException("Something went wrong, Latest (" + json.getLatest() + ") did not point to an entry in the json list: " + meta.getAbsolutePath());
            ret = new Artifact(artifact, this, json.getLatest());
        }

        return getFile(ret.getPath()).exists() ? ret : null;
    }

    public File getFile(String path)
    {
        return new File(root, path);
    }

    public File archive(Artifact artifact, File file, byte[] manifest)
    {
        File target = artifact.getFile();
        try
        {
            if (target.exists())
            {
                LOGGER.debug("Maven file already exists for {}({}) at {}, deleting duplicate.", file.getName(), artifact.toString(), target.getAbsolutePath());
                file.delete();
            }
            else
            {
                LOGGER.debug("Moving file {}({}) to maven repo at {}.", file.getName(), artifact.toString(), target.getAbsolutePath());
                Files.move(file, target);

                if (artifact.isSnapshot())
                {
                    SnapshotJson json = SnapshotJson.create(artifact.getSnapshotMeta());
                    json.add(new SnapshotJson.Entry(artifact.getTimestamp(), Files.hash(target, Hashing.md5()).toString()));
                    json.write(artifact.getSnapshotMeta());
                }

                if (!LibraryManager.DISABLE_EXTERNAL_MANIFEST)
                {
                    File meta_target = new File(target.getAbsolutePath() + ".meta");
                    Files.write(manifest, meta_target);
                }
            }
            return target;
        }
        catch (IOException e)
        {
            LOGGER.error(LOGGER.getMessageFactory().newMessage("Error moving file {} to {}", file, target.getAbsolutePath()), e);
        }
        return file;
    }

    public void filterLegacy(List<File> list){}
}
