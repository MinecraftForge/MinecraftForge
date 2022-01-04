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

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ObjectHolder;

@Mod(CustomPlantTypeTest.MODID)
@Mod.EventBusSubscriber(bus = Bus.MOD)
public class CustomPlantTypeTest
{
    static final String MODID = "custom_plant_type_test";
    private static final String CUSTOM_SOIL_BLOCK = "test_custom_soil";
    private static final String CUSTOM_PLANT_BLOCK = "test_custom_plant";

    @ObjectHolder(CUSTOM_SOIL_BLOCK)
    public static Block CUSTOM_SOIL;
    @ObjectHolder(CUSTOM_PLANT_BLOCK)
    public static Block CUSTOM_PLANT;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().registerAll(new Block(BlockBehaviour.Properties.of(Material.STONE)).setRegistryName(MODID, CUSTOM_SOIL_BLOCK), new CustomPlantBlock());
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(new BlockItem(CUSTOM_SOIL, (new Item.Properties())).setRegistryName(MODID, CUSTOM_SOIL_BLOCK),
                new BlockItem(CUSTOM_PLANT, (new Item.Properties())).setRegistryName(MODID, CUSTOM_PLANT_BLOCK));
    }

    public static class CustomPlantBlock extends FlowerBlock implements IPlantable
    {
        public static final PlantType CUSTOM_PLANT_TYPE = PlantType.get("custom_plant_type", (state, world, pos, facing, plantable) -> {
            return state.is(CUSTOM_SOIL);
        });

        public CustomPlantBlock()
        {
            super(MobEffects.UNLUCK, 14, Properties.of(Material.PLANT).noCollission().sound(SoundType.GRASS));
            this.setRegistryName(MODID, CUSTOM_PLANT_BLOCK);
        }

        @Override
        public PlantType getPlantType(BlockGetter world, BlockPos pos)
        {
            return CUSTOM_PLANT_TYPE;
        }

        @Override
        public BlockState getPlant(BlockGetter world, BlockPos pos)
        {
            return defaultBlockState();
        }
    }
}
