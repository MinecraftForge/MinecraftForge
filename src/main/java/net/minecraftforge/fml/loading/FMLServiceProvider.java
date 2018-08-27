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

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;
import cpw.mods.modlauncher.api.ITransformer;
import cpw.mods.modlauncher.api.IncompatibleEnvironmentException;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.OptionSpecBuilder;
import net.minecraftforge.fml.common.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import static net.minecraftforge.fml.Logging.CORE;

public class FMLServiceProvider implements ITransformationService
{

    private static final Logger LOGGER = LogManager.getLogger();
    private ArgumentAcceptingOptionSpec<String> modsOption;
    private ArgumentAcceptingOptionSpec<String> modListsOption;
    private List<String> modsArgumentList;
    private List<String> modListsArgumentList;

    @Override
    public String name()
    {
        return "fml";
    }

    @Override
    public void initialize(IEnvironment environment)
    {
        LOGGER.debug(CORE,"Setting up basic FML game directories");
        FMLPaths.setup(environment);
        LOGGER.debug(CORE,"Loading configuration");
        FMLConfig.load();
        LOGGER.debug(CORE, "Preparing launch handler");
        FMLLoader.setupLaunchHandler(environment);
        LOGGER.debug(CORE,"Initiating mod scan");
        FMLLoader.beginModScan();
        LOGGER.debug(CORE, "Loading access transformers");
        FMLLoader.loadAccessTransformer();
    }

    @Override
    public void onLoad(IEnvironment environment, Set<String> otherServices) throws IncompatibleEnvironmentException
    {
        FMLLoader.onInitialLoad(environment, otherServices);
    }

    @Override
    public void arguments(BiFunction<String, String, OptionSpecBuilder> argumentBuilder)
    {
        modsOption = argumentBuilder.apply("mods", "List of mods to add").withRequiredArg().ofType(String.class).withValuesSeparatedBy(",");
        modListsOption = argumentBuilder.apply("modLists", "JSON modlists").withRequiredArg().ofType(String.class).withValuesSeparatedBy(",");
    }

    @Override
    public void argumentValues(OptionResult option)
    {
        modsArgumentList = option.values(modsOption);
        modListsArgumentList = option.values(modListsOption);
    }

    @Nonnull
    @Override
    public List<ITransformer> transformers()
    {
        LOGGER.debug(CORE, "Loading coremod transformers");
        return new ArrayList<>(FMLLoader.getCoreModProvider().getCoreModTransformers());
    }

}
