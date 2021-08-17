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

package net.minecraftforge.debug.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@Mod(RedstoneSidedConnectivityTest.MODID)
@Mod.EventBusSubscriber(modid = RedstoneSidedConnectivityTest.MODID, bus = Bus.MOD)
public class RedstoneSidedConnectivityTest
{
    static final String MODID = "redstone_sided_connectivity_test";
    static final String BLOCK_ID = "test_east_redstone_connect";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    private static final RegistryObject<Block> eastRedstoneBlock = BLOCKS.register(BLOCK_ID, EastRedstoneBlock::new);

    static {
        ITEMS.register(BLOCK_ID, () -> new BlockItem(eastRedstoneBlock.get(), new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));
    }

    public RedstoneSidedConnectivityTest() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
    }

    private static class EastRedstoneBlock extends Block{

        //This block visually connect to redstone dust only on the east side
        //if a furnace block is placed on top of it

        public EastRedstoneBlock()
        {
            super(Properties.of(Material.METAL));
        }

        @Override
        public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, @Nullable Direction direction)
        {
            //The passed direction is relative to the redstone dust
            //This block connects on the east side relative to this block, which is west for the dust
            return direction == Direction.WEST &&
                    world.getBlockEntity(pos.relative(Direction.UP)) instanceof FurnaceBlockEntity;
        }
    }
}
