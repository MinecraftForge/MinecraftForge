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

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopperFullBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(CustomCopperTest.MODID)
public class CustomCopperTest
{
    public static final boolean ENABLED = true;
    public static final String MODID = "custom_copper_test";

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<Block> TEST_COPPER_BLOCK = BLOCKS.register("test_copper_block", () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.UNAFFECTED, Block.Properties.copy(Blocks.COPPER_BLOCK)));
    public static final RegistryObject<Block> TEST_OXIDIZED_COPPER_BLOCK = BLOCKS.register("test_oxidized_copper_block", () -> new WeatheringCopperFullBlock(WeatheringCopper.WeatherState.OXIDIZED, Block.Properties.copy(Blocks.OXIDIZED_COPPER)));

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> TEST_COPPER_BLOCK_ITEM = ITEMS.register("test_copper_block", () -> new BlockItem(TEST_COPPER_BLOCK.get(), (new Item.Properties())));
    public static final RegistryObject<Item> TEST_OXIDIZED_COPPER_BLOCK_ITEM = ITEMS.register("test_oxidized_copper_block", () -> new BlockItem(TEST_OXIDIZED_COPPER_BLOCK.get(), (new Item.Properties())));

    public CustomCopperTest()
    {
        if (ENABLED)
        {
            final IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
            MinecraftForge.EVENT_BUS.register(this);

            BLOCKS.register(eventBus);
            ITEMS.register(eventBus);

            eventBus.addListener(this::commonSetup);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        event.enqueueWork(() -> {
            WeatheringCopper.NEXT_BY_BLOCK.get().put(TEST_COPPER_BLOCK.get(), TEST_OXIDIZED_COPPER_BLOCK.get());
            HoneycombItem.WAXABLES.get().put(Blocks.DIRT, Blocks.SAND);
        });
    }
}
