/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.debug.world;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.api.distmarker.Dist;

import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.Collections;

@Mod(modid = TileEntityLoadingTest.MODID, name = "TileEntity#onLoad() test mod", version = "1.0", acceptableRemoteVersions = "*")
public class TileEntityLoadingTest
{
    public static final boolean ENABLED = false;
    static final String MODID = "te_loading_test";
    static final boolean DEBUG = false;

    private static Logger logger;
    @ObjectHolder(TestBlock.NAME)
    private static final Block TEST_BLOCK = null;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            if (!ENABLED) return;
            event.getRegistry().register(new TestBlock());
            GameRegistry.registerTileEntity(TestTE.class, (new ResourceLocation(MODID, TestBlock.NAME)).toString());
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            if (!ENABLED) return;
            event.getRegistry().register(new ItemBlock(TEST_BLOCK).setRegistryName(TEST_BLOCK.getRegistryName()));
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            if (!ENABLED) return;
            ModelLoader.setCustomStateMapper(TEST_BLOCK, block -> Collections.emptyMap());
            ModelBakery.registerItemVariants(Item.getItemFromBlock(TEST_BLOCK));
        }
    }

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        if (ENABLED)
            logger = event.getModLog();
    }

    public static class TestBlock extends Block
    {
        static final String NAME = "test_block";

        TestBlock()
        {
            super(Material.ANVIL);
            setRegistryName(NAME);
            setUnlocalizedName(MODID + "." + NAME);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
        }

        @Override
        public boolean hasTileEntity(IBlockState state)
        {
            return true;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(World world, IBlockState state)
        {
            return new TestTE();
        }
    }

    public static class TestTE extends TileEntity
    {
        @Override
        public void onLoad()
        {
            logger.info("World: {}, Pos: {}, State: {}", world, pos, world.getBlockState(pos));
            if (DEBUG)
            {
                logger.trace("Stack trace:", new Exception());
            }
        }
    }
}
