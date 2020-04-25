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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LinkRepository extends Repository
{
    private static final Logger LOGGER = LogManager.getLogger();

    private Map<String, File> artifact_to_file = new HashMap<>();
    private Map<String, File> filesystem = new HashMap<>();
    private Map<String, Artifact> snapshots = new HashMap<>();
    private Set<File> known = new HashSet<>();

    LinkRepository(File root)
    {
        super(root, "MEMORY");
    }

    public File archive(Artifact artifact, File file, byte[] manifest)
    {
        String key = artifact.toString();
        known.add(file);
        if (artifact_to_file.containsKey(key))
        {
            LOGGER.debug("Maven file already exists for {}({}) at {}, ignoring duplicate.", file.getName(), artifact.toString(), artifact_to_file.get(key).getAbsolutePath());

            if (artifact.isSnapshot())
            {
                Artifact old = snapshots.get(key);
                if (old == null || old.compareVersion(artifact) < 0)
                {
                    LOGGER.debug("Overriding Snapshot {} -> {}", old == null ? "null" : old.getTimestamp(), artifact.getTimestamp());
                    snapshots.put(key, artifact);
                    artifact_to_file.put(key, file);
                    filesystem.put(artifact.getPath(), file);
                }
            }
        }
        else
        {
            LOGGER.debug("Making maven link for {} in memory to {}.", key, file.getAbsolutePath());
            artifact_to_file.put(key, file);
            filesystem.put(artifact.getPath(), file);

            if (artifact.isSnapshot())
                snapshots.put(key, artifact);

            /* Support external meta? screw it.
            if (!LibraryManager.DISABLE_EXTERNAL_MANIFEST)
            {
                File meta_target = new File(target.getAbsolutePath() + ".meta");
                Files.write(manifest, meta_target);
            }
            */
        }
        return file;
    }

    @Override
    public void filterLegacy(List<File> list)
    {
        list.removeIf(e -> known.contains(e));
    }

    public Artifact resolve(Artifact artifact)
    {
        String key = artifact.toString();
        File file = artifact_to_file.get(key);
        if (file == null || !file.exists())
            return super.resolve(artifact);
        return new Artifact(artifact, this, artifact.isSnapshot() ? artifact.getTimestamp() : null);
    }

    @Override
    public File getFile(String path)
    {
        return filesystem.containsKey(path) ? filesystem.get(path) : super.getFile(path);
    }
}
