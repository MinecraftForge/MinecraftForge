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
package net.minecraftforge.fml.relauncher.libraries;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class Repository
{
    private static final Map<String, Repository> repo_list = new LinkedHashMap<>();
    public static Repository create(File root) throws IOException
    {
        return create(root, root.getCanonicalPath());
    }
    public static Repository create(File root, String name)
    {
        return repo_list.computeIfAbsent(name, f -> new Repository(root, name));
    }
    public static Repository replace(File root, String name)
    {
        return repo_list.put(name, new Repository(root, name));
    }
    public static Repository get(String name)
    {
        return repo_list.get(name);
    }
    public static Artifact resolveAll(Artifact artifact)
    {
        Artifact ret = null;
        for (Repository repo : repo_list.values())
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

    private Repository(File root) throws IOException
    {
        this(root, root.getCanonicalPath());
    }
    private Repository(File root, String name)
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
}
