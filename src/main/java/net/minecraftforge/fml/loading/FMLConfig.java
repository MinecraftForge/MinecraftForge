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

package net.minecraftforge.fml.loading;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.FMLPaths;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;

import static net.minecraftforge.fml.Logging.CORE;

public class FMLConfig
{
    private static FMLConfig INSTANCE = new FMLConfig();
    private static ConfigSpec configSpec = new ConfigSpec();
    static {
        configSpec.define("splashscreen", Boolean.TRUE);
        configSpec.defineInList("side", Dist.CLIENT.name(), Arrays.stream(Dist.values()).map(Enum::name).collect(Collectors.toList()));
        configSpec.defineInRange("maxframerate", 60, 10, 120);
        configSpec.defineInRange("minframerate", 60, 10, 120);
        configSpec.defineInList(Arrays.asList("tasty","flavour"), Dist.CLIENT.name(), Arrays.stream(Dist.values()).map(Enum::name).collect(Collectors.toList()));
        configSpec.defineInList(Arrays.asList("tasty","teaser"), Dist.CLIENT.name(), Arrays.stream(Dist.values()).map(Enum::name).collect(Collectors.toList()));
        configSpec.define("longstring", StringUtils.repeat("AAAA", 10000), s->s!=null && ((String)s).length()>0);
    }

    private CommentedFileConfig configData;

    private void loadFrom(final Path configFile)
    {
        configData = CommentedFileConfig.builder(configFile).sync().
                defaultResource("/META-INF/defaultfmlconfig.toml").
                autosave().autoreload().
                writingMode(WritingMode.REPLACE).
                build();
        configData.load();
        if (!configSpec.isCorrect(configData)) {
            LogManager.getLogger().warn(CORE, "Configuration file {} is not correct. Correcting", configFile);
            configSpec.correct(configData, (action, path, incorrectValue, correctedValue) ->
                    LogManager.getLogger().warn(CORE, "Incorrect key {} was corrected from {} to {}", path, incorrectValue, correctedValue));
        }
        configData.save();
    }

    public static void load()
    {
        final Path configFile = FMLPaths.FMLCONFIG.get();
        INSTANCE.loadFrom(configFile);
        LogManager.getLogger().debug(CORE, "Loaded FML config from {}", FMLPaths.FMLCONFIG.get());
        LogManager.getLogger().debug(CORE, "Splash screen is {}", INSTANCE.splashScreenEnabled());
    }

    public boolean splashScreenEnabled() {
        return configData.<Boolean>getOptional("splashscreen").orElse(Boolean.FALSE);
    }
}
