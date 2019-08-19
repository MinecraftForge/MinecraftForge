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
 *//*


package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import javax.annotation.Nullable;

//This adds a block that creates a 4x4x4 watered region when activated
@Mod(FarmlandWaterTest.MOD_ID)
@Mod.EventBusSubscriber
public class FarmlandWaterTest
{
    static final String MOD_ID = "farmland_water_test";
    static final String BLOCK_ID = "test_block";
    static final String TILE_ID = "test_tile";

    private static Logger logger = LogManager.getLogger(FarmlandWaterTest.class);

    @ObjectHolder(BLOCK_ID)
    public static final Block test_block = null;
    @ObjectHolder(TILE_ID)
    public static final TileEntityType<TestTileEntity> test_tile = null;

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new TestBlock(Block.Properties.create(Material.ROCK)).setRegistryName(MOD_ID, BLOCK_ID));
    }

    @SubscribeEvent
    public static void registerTileEntity(RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().register(TileEntityType.Builder.func_223042_a(TestTileEntity::new, test_block).build(null).setRegistryName(MOD_ID, TILE_ID));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new BlockItem(test_block, new Item.Properties()).setRegistryName(new ResourceLocation(MOD_ID, BLOCK_ID)));
    }

    public static class TestBlock extends Block
    {
        public TestBlock(Block.Properties props)
        {
            super(props);
        }

        @Override
        public boolean hasTileEntity(BlockState state)
        {
            return true;
        }

        @Nullable
        @Override
        public TileEntity createTileEntity(BlockState state, IBlockReader world)
        {
            return new TestTileEntity();
        }

        @Override
        public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTrace)
        {
            Optional<TestTileEntity> opt  = getTE(world, pos);
            opt.ifPresent(tileEntity -> {
                tileEntity.isActive = !tileEntity.isActive;
                tileEntity.updateTicket();
                player.sendStatusMessage(new StringTextComponent("Changed block powered state to " + tileEntity.isActive), true);
                logger.info("Changed block powered state at {} to {}", pos, tileEntity.isActive);
            });
            return opt.isPresent();
        }

        @Override
        public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
        {
            getTE(world, pos).ifPresent(TileEntity::remove);
        }

        private Optional<TestTileEntity> getTE(World world, BlockPos pos)
        {
            if (world.isRemote) return Optional.empty();
            return Optional.ofNullable((TestTileEntity)world.getTileEntity(pos));
        }
    }

    public static class TestTileEntity extends TileEntity
    {
        private AABBTicket farmlandTicket;
        private boolean isActive = false;

        public TestTileEntity()
        {
            super(FarmlandWaterTest.test_tile);
        }

        @Override
        public void onLoad()
        {
            if (!world.isRemote)
            {
                farmlandTicket = FarmlandWaterManager.addAABBTicket(world, new AxisAlignedBB(pos).grow(4D));
                updateTicket();
            }
        }

        private void updateTicket()
        {
            if (world.isRemote)
                return;
            if (isActive)
                farmlandTicket.validate();
            else
                farmlandTicket.invalidate();
        }

        @Override
        public CompoundNBT write(CompoundNBT compound)
        {
            compound = super.write(compound);
            compound.putBoolean("active", isActive);
            return compound;
        }

        @Override
        public void read(CompoundNBT compound)
        {
            super.read(compound);
            isActive = compound.getBoolean("active");
        }

        @Override
        public void remove()
        {
            if (!world.isRemote)
            {
                farmlandTicket.invalidate();
            }
        }
    }
}
*/
