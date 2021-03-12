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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import net.minecraftforge.versions.mcp.MCPVersion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public class ModList
{
    private static final Logger LOGGER = LogManager.getLogger();

    private static final Gson GSON = new GsonBuilder().setLenient().create();
    static final Map<String, ModList> cache = new HashMap<>();

    public static ModList create(File json, File mcdir)
    {
        try
        {
            String key = json.getCanonicalFile().getAbsolutePath();
            return cache.computeIfAbsent(key, k -> new ModList(json, mcdir));
        }
        catch (IOException e)
        {
            LOGGER.error(LOGGER.getMessageFactory().newMessage("Unable to load ModList json at {}.", json.getAbsoluteFile()), e);
        }

        return new ModList(json, mcdir);
    }

    @SuppressWarnings("unchecked")
    public static List<ModList> getKnownLists(File mcdir)
    {
        for (String list : new String[] {"mods/mod_list.json", "mods/" + MCPVersion.getMCVersion() + "/mod_list.json"/* , TODO Launch args ((Map<String, String>)Launch.blackboard.get("launchArgs")).get("--modListFile")*/})
        {
            if (list != null)
            {
                File listFile = getFile(mcdir, list);
                if (listFile != null && listFile.exists())
                    create(listFile, mcdir); //Create will recursively create parent lists, so after this run everything should be populated.
            }
        }
        return ImmutableList.copyOf(cache.values()); //TODO: Do we care about order? I dont think so as we resolve all libs in a flat map.
    }

    @SuppressWarnings("unchecked")
    public static List<ModList> getBasicLists(File mcdir)
    {
        List<ModList> lst = new ArrayList<>();

        ModList memory = cache.get("MEMORY");
        if (memory != null)
            lst.add(memory);

        for (String list : new String[] {"mods/mod_list.json", "mods/" + MCPVersion.getMCVersion() + "/mod_list.json"/* TODO Launch args, ((Map<String, String>)Launch.blackboard.get("launchArgs")).get("--modListFile")*/})
        {
            if (list != null)
            {
                File listFile = getFile(mcdir, list);
                if (listFile != null && listFile.exists())
                    lst.add(create(listFile, mcdir));
            }
        }
        return lst;
    }

    private final File path;
    private final JsonModList mod_list;
    private final Repository repo;
    private final ModList parent;
    private final List<Artifact> artifacts = new ArrayList<>();
    private final List<Artifact> artifacts_imm = Collections.unmodifiableList(artifacts);
    private final Map<String, Artifact> art_map = new HashMap<>();

    private boolean changed = false;

    protected ModList(Repository repo)
    {
        this.path = null;
        this.mod_list = new JsonModList();
        this.repo = repo;
        this.parent = null;
    }

    private ModList(File path, File mcdir)
    {
        this.path = path;
        JsonModList temp_list = null;
        if (this.path.exists())
        {
            try
            {
                String json = Files.asCharSource(path, StandardCharsets.UTF_8).read();
                temp_list = GSON.fromJson(json, JsonModList.class);
            }
            catch (JsonSyntaxException jse)
            {
                LOGGER.info(LOGGER.getMessageFactory().newMessage("Failed to parse modList json file {}.", path), jse);
            }
            catch (IOException ioe)
            {
                LOGGER.info(LOGGER.getMessageFactory().newMessage("Failed to read modList json file {}.", path), ioe);
            }
        }
        this.mod_list = temp_list == null ? new JsonModList() : temp_list;
        Repository temp = null;
        if (mod_list.repositoryRoot != null)
        {
            try
            {
                File repoFile = getFile(mcdir, this.mod_list.repositoryRoot);
                if (repoFile != null)
                {
                    temp = Repository.create(repoFile);
                }
            }
            catch (IOException e)
            {
                LOGGER.info(LOGGER.getMessageFactory().newMessage("Failed to create repository for modlist at {}.", mod_list.repositoryRoot), e);
            }
        }
        this.repo = temp;
        File parent_path = this.mod_list.parentList == null ? null : getFile(mcdir, this.mod_list.parentList);
        this.parent = parent_path == null || !parent_path.exists() ? null : ModList.create(parent_path, mcdir);

        if (mod_list.modRef != null)
        {
            for (String ref : mod_list.modRef)
                add(new Artifact(this.getRepository(), ref, null));
            changed = false;
        }
    }

    public Repository getRepository()
    {
        return this.repo;
    }

    public void add(Artifact artifact)
    {
        Artifact old = art_map.get(artifact.toString());
        if (old != null)
        {
            artifacts.add(artifacts.indexOf(old), artifact);
            artifacts.remove(old);
        }
        else
            artifacts.add(artifact);
        art_map.put(artifact.toString(), artifact);
        changed = true;
    }

    public List<Artifact> getArtifacts()
    {
        return artifacts_imm;
    }

    public boolean changed()
    {
        return changed;
    }

    public void save() throws IOException
    {
        mod_list.modRef = artifacts.stream().map(a -> a.toString()).collect(Collectors.toList());
        Files.write(GSON.toJson(mod_list), path, StandardCharsets.UTF_8);
    }

    private static File getFile(File root, String path)
    {
        try
        {
            if (path.startsWith("absolute:"))
                return new File(path.substring(9)).getCanonicalFile();
            else
                return new File(root, path).getCanonicalFile();
        }
        catch (IOException ioe)
        {
            LOGGER.info(LOGGER.getMessageFactory().newMessage("Unable to canonicalize path {} relative to {}", path, root.getAbsolutePath()));
        }
        return null;
    }

    private static class JsonModList
    {
        public String repositoryRoot;
        public List<String> modRef;
        public String parentList;
    }

    //TODO: Some form of caching?
    public List<Artifact> flatten()
    {
        List<Artifact> lst = parent == null ? new ArrayList<>() : parent.flatten();
        for (Artifact art : artifacts)
        {
            Optional<Artifact> old = lst.stream().filter(art::matchesID).findFirst();
            if (!old.isPresent())
            {
                lst.add(art);
            }
            else if (old.get().getVersion().compareTo(art.getVersion()) < 0)
            {
                lst.add(lst.indexOf(old.get()), art);
                lst.remove(old.get());
            }
        }
        return lst;
    }

    public Object getName()
    {
        return path.getAbsolutePath();
    }
}
