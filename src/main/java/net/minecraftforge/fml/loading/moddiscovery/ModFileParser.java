/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

package net.minecraftforge.fml.loading.moddiscovery;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.minecraftforge.fml.Logging.LOADING;
import static net.minecraftforge.fml.Logging.fmlLog;

public class ModFileParser {
    protected static List<ModInfo> readModList(final ModFile modFile) {
        fmlLog.debug(LOADING,"Parsing mod file candidate {}", modFile.getFilePath());
        try {
            final Path modsjson = modFile.getLocator().findPath(modFile, "META-INF", "mods.json");
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(ModInfo.class, (InstanceCreator<ModInfo>)ic -> new ModInfo(modFile, null, null, null, null, null, null, null));
            gsonBuilder.registerTypeAdapter(ArtifactVersion.class, (JsonDeserializer<ArtifactVersion>) (element, type, context) -> new DefaultArtifactVersion(element.getAsString()));
            Gson gson = gsonBuilder.create();
            final ModInfo[] modInfos = gson.fromJson(Files.newBufferedReader(modsjson), ModInfo[].class);
            return Stream.of(modInfos).collect(Collectors.toList());
        } catch (IOException e) {
            fmlLog.debug(LOADING,"Ignoring invalid JAR file {}", modFile.getFilePath());
            return Collections.emptyList();
        }
    }

    protected static List<CoreModFile> getCoreMods(final ModFile modFile) {
        Map<String,String> coreModPaths;
        try {
            final Path coremodsjson = modFile.getLocator().findPath(modFile, "META-INF", "coremods.json");
            if (!Files.exists(coremodsjson)) {
                return Collections.emptyList();
            }
            final Type type = new TypeToken<Map<String, String>>() {}.getType();
            final Gson gson = new Gson();
            coreModPaths = gson.fromJson(Files.newBufferedReader(coremodsjson), type);
        } catch (IOException e) {
            fmlLog.debug(LOADING,"Failed to read coremod list coremods.json", e);
            return Collections.emptyList();
        }

        return coreModPaths.entrySet().stream().
                peek(e-> fmlLog.debug(LOADING,"Found coremod {} with Javascript path {}", e.getKey(), e.getValue())).
                map(e -> new CoreModFile(e.getKey(), modFile.getLocator().findPath(modFile, e.getValue()),modFile)).
                collect(Collectors.toList());
    }
}