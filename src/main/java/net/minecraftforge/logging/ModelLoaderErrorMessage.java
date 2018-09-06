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

package net.minecraftforge.logging;

import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.registries.ForgeRegistries;

import org.apache.logging.log4j.message.SimpleMessage;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static net.minecraftforge.client.model.ModelLoader.getInventoryVariant;

public class ModelLoaderErrorMessage extends SimpleMessage
{
    private final ModelResourceLocation resourceLocation;
    private final Exception exception;
    private final IRegistry<ModelResourceLocation, IBakedModel> modelRegistry;

    private static Multimap<ModelResourceLocation, IBlockState> reverseBlockMap = HashMultimap.create();
    private static Multimap<ModelResourceLocation, String> reverseItemMap = HashMultimap.create();

    private static void buildLookups(final BlockModelShapes blockModelShapes, Function<Item,Iterable<String>> itemNameLookup) {
        if (!reverseBlockMap.isEmpty()) return;
        for(Map.Entry<IBlockState, ModelResourceLocation> entry : blockModelShapes.getBlockStateMapper().putAllStateModelLocations().entrySet())
        {
            reverseBlockMap.put(entry.getValue(), entry.getKey());
        }
        ForgeRegistries.ITEMS.forEach(item ->
        {
            for(String s : itemNameLookup.apply(item))
            {
                ModelResourceLocation memory = getInventoryVariant(s);
                reverseItemMap.put(memory, item.getRegistryName().toString());
            }
        });

    }

    public ModelLoaderErrorMessage(ModelResourceLocation resourceLocation, Exception exception, IRegistry<ModelResourceLocation, IBakedModel> modelRegistry, BlockModelShapes blockModelShapes, Function<Item, Iterable<String>> itemNameLookup)
    {
        // if we're logging these error messages, this will get built for reference
        buildLookups(blockModelShapes, itemNameLookup);
        this.resourceLocation = resourceLocation;
        this.exception = exception;
        this.modelRegistry = modelRegistry;
    }

    private void stuffs() {
        String domain = resourceLocation.getResourceDomain();
        String errorMsg = "Exception loading model for variant " + resourceLocation;
        Collection<IBlockState> blocks = reverseBlockMap.get(resourceLocation);
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
//            FMLLog.log.error("{}, normal location exception: ", errorMsg, ex.normalException);
//            FMLLog.log.error("{}, blockstate location exception: ", errorMsg, ex.blockstateException);
        }
        else
        {
//            FMLLog.log.error(errorMsg, entry.getValue());
        }
//        ResourceLocation blockstateLocation = new ResourceLocation(resourceLocation.getResourceDomain(), resourceLocation.getResourcePath());
//        if(loadingExceptions.containsKey(blockstateLocation) && !printedBlockStateErrors.contains(blockstateLocation))
//        {
//            FMLLog.log.error("Exception loading blockstate for the variant {}: ", location, loadingExceptions.get(blockstateLocation));
//            printedBlockStateErrors.add(blockstateLocation);
//        }
    }
    @Override
    public void formatTo(StringBuilder buffer)
    {

    }
}
