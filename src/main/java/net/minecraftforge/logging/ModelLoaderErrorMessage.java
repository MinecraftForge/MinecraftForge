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

package net.minecraftforge.logging;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.message.SimpleMessage;

import java.util.Collection;

import static net.minecraftforge.client.model.ModelLoader.getInventoryVariant;

public class ModelLoaderErrorMessage extends SimpleMessage
{
    private final ModelResourceLocation resourceLocation;
    private final Exception exception;

    private static Multimap<ModelResourceLocation, BlockState> reverseBlockMap = HashMultimap.create();
    private static Multimap<ModelResourceLocation, String> reverseItemMap = HashMultimap.create();

    private static void buildLookups() {
        if (!reverseBlockMap.isEmpty()) return;
        
        ForgeRegistries.BLOCKS.getValues().stream()
        	.flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
        	.forEach(state -> reverseBlockMap.put(BlockModelShaper.stateToModelLocation(state), state));

        ForgeRegistries.ITEMS.forEach(item ->
        {
        	ModelResourceLocation memory = getInventoryVariant(ForgeRegistries.ITEMS.getKey(item).toString());
        	reverseItemMap.put(memory, item.getRegistryName().toString());
        });

    }

    public ModelLoaderErrorMessage(ModelResourceLocation resourceLocation, Exception exception)
    {
        // if we're logging these error messages, this will get built for reference
        buildLookups();
        this.resourceLocation = resourceLocation;
        this.exception = exception;
    }

    private void stuffs() {
        String domain = resourceLocation.getNamespace();
        String errorMsg = "Exception loading model for variant " + resourceLocation;
        Collection<BlockState> blocks = reverseBlockMap.get(resourceLocation);
        if(!blocks.isEmpty())
        {
            if(blocks.size() == 1)
            {
                errorMsg += " for blockstate \"" + blocks.iterator().next() + "\"";
            }
            else
            {
                errorMsg += " for blockstates [\"" + Joiner.on("\", \"").join(blocks) + "\"]";
            }
        }
        Collection<String> items = reverseItemMap.get(resourceLocation);
        if(!items.isEmpty())
        {
            if(!blocks.isEmpty()) errorMsg += " and";
            if(items.size() == 1)
            {
                errorMsg += " for item \"" + items.iterator().next() + "\"";
            }
            else
            {
                errorMsg += " for items [\"" + Joiner.on("\", \"").join(items) + "\"]";
            }
        }
        if(exception instanceof ModelLoader.ItemLoadingException)
        {
            ModelLoader.ItemLoadingException ex = (ModelLoader.ItemLoadingException)exception;
//            LOGGER.error("{}, normal location exception: ", errorMsg, ex.normalException);
//            LOGGER.error("{}, blockstate location exception: ", errorMsg, ex.blockstateException);
        }
        else
        {
//            LOGGER.error(errorMsg, entry.getValue());
        }
//        ResourceLocation blockstateLocation = new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
//        if(loadingExceptions.containsKey(blockstateLocation) && !printedBlockStateErrors.contains(blockstateLocation))
//        {
//            LOGGER.error("Exception loading blockstate for the variant {}: ", location, loadingExceptions.get(blockstateLocation));
//            printedBlockStateErrors.add(blockstateLocation);
//        }
    }
    @Override
    public void formatTo(StringBuilder buffer)
    {

    }
}
