/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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
package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.data.*;
import net.minecraftforge.client.model.generators.*;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.*;
import net.minecraftforge.registries.ObjectHolder;

@SuppressWarnings("unused")
@Mod(TrapdoorTest.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class TrapdoorTest
{
    public static final String MODID = "trapdoor_test";
    public static final String LADDER = "trapdoor_test_ladder";

    @ObjectHolder("trapdoor_test:trapdoor_test_ladder")
    private static Block LADDER_BLOCK;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new TrapdoorTestLadderBlock());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BlockItem(LADDER_BLOCK, new Item.Properties()).setRegistryName(MODID, LADDER));
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event)
    {
        RenderTypeLookup.setRenderLayer(LADDER_BLOCK, RenderType.getCutout());
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        if (event.includeClient())
        {
            generator.addProvider(new BlockStates(generator, event.getExistingFileHelper()));
            generator.addProvider(new ItemModels(generator, event.getExistingFileHelper()));
        }
    }

    private static class TrapdoorTestLadderBlock extends LadderBlock
    {
        public TrapdoorTestLadderBlock()
        {
            super(Block.Properties.create(Material.WOOL).notSolid());
            setRegistryName(LADDER);
        }

        public TrapdoorTestLadderBlock(Block.Properties props)
        {
            super(props);
        }
    }

    public static class BlockStates extends BlockStateProvider
    {
        public BlockStates(DataGenerator generator, ExistingFileHelper efh)
        {
            super(generator, MODID, efh);
        }

        @Override
        public String getName()
        {
            return "Trapdoor Test BlockStates";
        }

        @Override
        protected void registerStatesAndModels()
        {
            horizontalBlock(LADDER_BLOCK, new ModelFile.UncheckedModelFile("minecraft:block/ladder"));
        }
    }

    public static class ItemModels extends ItemModelProvider
    {
        public ItemModels(DataGenerator generator, ExistingFileHelper efh)
        {
            super(generator, MODID, efh);
        }

        @Override
        public String getName()
        {
            return "Trapdoor Test Item Models";
        }

        @Override
        protected void registerModels()
        {
            getBuilder(LADDER).parent(getExistingFile(mcLoc("block/ladder")));
        }
    }
}
