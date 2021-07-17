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

package net.minecraftforge.debug.client.model;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;

@Mod(CompositeModelTest.MODID)
public class CompositeModelTest
{
    public static final String MODID = "composite_model_test";
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static RegistryObject<Block> composite_block = BLOCKS.register("composite_block", () ->
            new Block(Block.Properties.of(Material.WOOD).strength(10)) {
                @Override
                protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
                {
                    builder.add(BlockStateProperties.HORIZONTAL_FACING);
                }

                @Nullable
                @Override
                public BlockState getStateForPlacement(BlockPlaceContext context)
                {
                    return defaultBlockState().setValue(
                            BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection()
                    );
                }

                @Override
                public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
                    return Shapes.or(
                            box(5.6, 5.6, 5.6, 10.4, 10.4, 10.4),
                            box(0, 0, 0, 4.8, 4.8, 4.8),
                            box(11.2, 0, 0, 16, 4.8, 4.8),
                            box(0, 0, 11.2, 4.8, 4.8, 16),
                            box(11.2, 0, 11.2, 16, 4.8, 16),
                            box(0, 11.2, 0, 4.8, 16, 4.8),
                            box(11.2, 11.2, 0, 16, 16, 4.8),
                            box(0, 11.2, 11.2, 4.8, 16, 16),
                            box(11.2, 11.2, 11.2, 16, 16, 16)
                    );
                }
            }
    );

    public static RegistryObject<Item> composite_item = ITEMS.register("composite_block", () ->
            new BlockItem(composite_block.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)) {
                @Override
                public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity)
                {
                    return armorType == EquipmentSlot.HEAD;
                }
            }
    );

    public CompositeModelTest()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
    }
}
