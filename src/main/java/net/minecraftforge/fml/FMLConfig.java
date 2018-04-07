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

package net.minecraftforge.fml;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.common.FMLPaths;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static net.minecraftforge.fml.Logging.CORE;
import static net.minecraftforge.fml.Logging.fmlLog;

public class FMLConfig
{
    private static FMLConfig INSTANCE;
    private final Map<String, String> configData = new HashMap<>();

    private FMLConfig() {
        configData.putAll(defaultValues());
    }

    private Map<String,String> defaultValues() {
        final Map<String,String> result = new HashMap<>();
        result.put("splashscreen", "true");
        return result;
    }

    private void loadFrom(final Path configFile) throws IOException
    {
        final Type type = new TypeToken<Map<String, String>>() {}.getType();
        final Gson gson = new Gson();
        final Map<String,String> loadedConfig = gson.fromJson(Files.newBufferedReader(configFile), type);
        if (loadedConfig != null)
            configData.putAll(loadedConfig);
    }

    private void saveConfigIfNecessary(final Path configFile) throws IOException {
        final Type type = new TypeToken<Map<String, String>>() {}.getType();
        final Gson gson = new Gson();
        final BufferedWriter writer = Files.newBufferedWriter(configFile);
        gson.toJson(configData, type, writer);
        writer.flush();
    }
    public static void load()
    {
        final Path configFile = FMLPaths.FMLCONFIG.get();
        INSTANCE = new FMLConfig();
        try
        {
            if (Files.exists(configFile))
            {
                INSTANCE.loadFrom(configFile);
            }
            INSTANCE.saveConfigIfNecessary(configFile);
        }
        catch (IOException ioe)
        {
            fmlLog.error(CORE,"Unable to read FML config at {}", configFile, ioe);
            throw new RuntimeException("Unable to read FML config", ioe);
        }
    }
}
