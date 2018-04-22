/*
 * Minecraft Forge
 * Copyright (c) 2016.
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

package net.minecraftforge.fml.common;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.VersionParser;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import javax.annotation.Nullable;

public class MetadataCollection
{
    private String modListVersion;
    private ModMetadata[] modList;
    private Map<String, ModMetadata> metadatas = Maps.newHashMap();

    public static MetadataCollection from(@Nullable InputStream inputStream, String sourceName)
    {
        if (inputStream == null)
        {
            return new MetadataCollection();
        }

        InputStreamReader reader = new InputStreamReader(inputStream);
        try
        {
            MetadataCollection collection;
            Gson gson = new GsonBuilder().registerTypeAdapter(ArtifactVersion.class, new ArtifactVersionAdapter()).create();
            JsonParser parser = new JsonParser();
            JsonElement rootElement = parser.parse(reader);
            if (rootElement.isJsonArray())
            {
                collection = new MetadataCollection();
                JsonArray jsonList = rootElement.getAsJsonArray();
                collection.modList = new ModMetadata[jsonList.size()];
                int i = 0;
                for (JsonElement mod : jsonList)
                {
                    collection.modList[i++]=gson.fromJson(mod, ModMetadata.class);
                }
            }
            else
            {
                collection = gson.fromJson(rootElement, MetadataCollection.class);
            }
            collection.parseModMetadataList();
            return collection;
        }
        catch (JsonParseException e)
        {
            FMLLog.log.error("The mcmod.info file in {} cannot be parsed as valid JSON. It will be ignored", sourceName, e);
            return new MetadataCollection();
        }
    }

    private void parseModMetadataList()
    {
        for (ModMetadata modMetadata : modList)
        {
            metadatas.put(modMetadata.modId, modMetadata);
        }
    }

    public ModMetadata getMetadataForId(String modId, Map<String, Object> extraData)
    {
        if (!metadatas.containsKey(modId))
        {
            ModMetadata dummy = new ModMetadata();
            dummy.modId = modId;
            dummy.name = (String) extraData.get("name");
            dummy.version = (String) extraData.get("version");
            dummy.autogenerated = true;
            metadatas.put(modId, dummy);
        }
        return metadatas.get(modId);
    }

    public static class ArtifactVersionAdapter extends TypeAdapter<ArtifactVersion>
    {

        @Override
        public void write(JsonWriter out, ArtifactVersion value) throws IOException
        {
            // no op - we never write these out
        }

        @Override
        public ArtifactVersion read(JsonReader in) throws IOException
        {
            return VersionParser.parseVersionReference(in.nextString());
        }

    }
}
