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
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod("be_onload_test")
public class BlockEntityOnLoadTest
{
    private static final boolean ENABLED = true;

    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "be_onload_test");
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "be_onload_test");
    private static final DeferredRegister<BlockEntityType<?>> BE_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, "be_onload_test");

    private static final RegistryObject<Block> TEST_BLOCK = BLOCKS.register("be_onload_testblock", () -> new TestBlock(Properties.of(Material.DECORATION, MaterialColor.SAND)));
    private static final RegistryObject<Item> TEST_BLOCK_ITEM = ITEMS.register("be_onload_testblock", () -> new BlockItem(TEST_BLOCK.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));
    private static final RegistryObject<BlockEntityType<TestBlockEntity>> TEST_BE_TYPE = BE_TYPES.register("be_onload_testbe", () -> BlockEntityType.Builder.of(TestBlockEntity::new, TEST_BLOCK.get()).build(null));

    public BlockEntityOnLoadTest()
    {
        if (ENABLED)
        {
            IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
            BLOCKS.register(modBus);
            ITEMS.register(modBus);
            BE_TYPES.register(modBus);
        }
    }

    private static class TestBlock extends Block implements EntityBlock
    {
        public TestBlock(Properties props) { super(props); }

        @Override
        public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
        {
            LOGGER.info("[BE_ONLOAD] Block#use at pos {} for {}", pos, level.getBlockEntity(pos));
            return super.use(state, level, pos, player, hand, hit);
        }

        @Override
        @Nullable
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
        {
            return new TestBlockEntity(pos, state);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
        {
            return (beLevel, bePos, beState, be) -> ((TestBlockEntity) be).tick();
        }
    }

    private static class TestBlockEntity extends BlockEntity
    {
        private static final Logger LOGGER = LogManager.getLogger();
        private boolean loaded = false;

        public TestBlockEntity(BlockPos pos, BlockState state)
        {
            super(TEST_BE_TYPE.get(), pos, state);
        }

        @Override
        public void onLoad()
        {
            LOGGER.info("[BE_ONLOAD] BlockEntity#onLoad at pos {} for {}", worldPosition, this);
            getLevel().setBlockAndUpdate(worldPosition.above(), Blocks.SAND.defaultBlockState());
            loaded = true;
        }

        private boolean first = true;
        public void tick()
        {
            if (first)
            {
                first = false;
                LOGGER.info("[BE_ONLOAD] TestBlockEntity#tick at pos {} for {}", worldPosition, this);
                if (!loaded)
                {
                    throw new IllegalStateException(String.format("BlockEntity at %s ticked before onLoad()!", getBlockPos()));
                }
            }
        }
    }
}
