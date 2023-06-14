/*
 * Minecraft Forge - Forge Development LLC
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.fmlonlyserver;

import joptsimple.OptionParser;
import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.ModLoadingWarning;
import net.minecraftforge.fml.ModWorkManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;

import static net.minecraftforge.fml.Logging.LOADING;

public class ServerModLoader
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean hasErrors = false;

    public static void addOptions(OptionParser parser)
    {
        // We consume this argument as we use it in the launcher and the client side.
        parser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."));
    }

    public static void load()
    {
        try
        {
            ModLoader.get().gatherAndInitializeMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), () -> {});
            ModLoader.get().loadMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), () -> {});
            ModLoader.get().finishMods(ModWorkManager.syncExecutor(), ModWorkManager.parallelExecutor(), () -> {});
        } catch (LoadingFailedException error)
        {
            ServerModLoader.hasErrors = true;
            throw error;
        }
        List<ModLoadingWarning> warnings = ModLoader.get().getWarnings();
        if (!warnings.isEmpty())
        {
            LOGGER.warn(LOADING, "Mods loaded with {} warnings", warnings.size());
            warnings.forEach(warning -> LOGGER.warn(LOADING, warning.formatToString()));
        }
    }

    public static boolean hasErrors()
    {
        return ServerModLoader.hasErrors;
    }
}
