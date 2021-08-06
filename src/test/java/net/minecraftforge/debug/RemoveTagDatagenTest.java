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

package net.minecraftforge.debug;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod(RemoveTagDatagenTest.MODID)
public class RemoveTagDatagenTest
{
    public static final String MODID = "remove_tag_datagen_test";
    public static final Tag.Named<Block> TEST_TAG = BlockTags.bind("test_tag"); 
    
    public RemoveTagDatagenTest()
    {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::onGatherData);
    }
    
    private void onGatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();
        
        generator.addProvider(new BlockTagsProvider(generator, MODID, helper)
        {
            @SuppressWarnings("unchecked")
            @Override
            protected void addTags()
            {
                this.tag(TEST_TAG)
                    .remove(Blocks.DIRT.getRegistryName())
                    .remove(Blocks.OAK_DOOR.getRegistryName(), Blocks.DARK_OAK_DOOR.getRegistryName())
                    .remove(Blocks.ANVIL)
                    .remove(Blocks.BASALT, Blocks.POLISHED_BASALT)
                    .remove(BlockTags.BEEHIVES)
                    .remove(BlockTags.BANNERS, BlockTags.BEDS);
            }
        });
    }
}
