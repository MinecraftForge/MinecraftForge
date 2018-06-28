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

package net.minecraftforge.debug.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.FarmlandWaterManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ticket.AABBTicket;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(modid = FarmlandWaterTest.ID, name = "Farmland Water Test", version = "1.0.0", acceptableRemoteVersions = "*")
public class FarmlandWaterTest
{
    //This adds a block that creates a 4x4x4 watered region when activated
    private static Logger logger;
    private static Block testBlock;
    static final String ID = "farmlandwatertest";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
        MinecraftForge.EVENT_BUS.register(FarmlandWaterTest.class);
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        testBlock = new TestBlock();
        event.getRegistry().register(testBlock.setRegistryName(new ResourceLocation(ID, "test_block")).setCreativeTab(CreativeTabs.MISC).setUnlocalizedName("Farmland Water Test Block"));
        GameRegistry.registerTileEntity(TestTileEntity.class, new ResourceLocation(ID, "test_te"));
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event)
    {
        event.getRegistry().register(new ItemBlock(testBlock).setRegistryName(new ResourceLocation(ID, "test_block")));
    }

    public static class TestBlock extends Block
    {

        public TestBlock()
        {
            super(Material.ROCK);
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
            return new TestTileEntity();
        }

        @Override
        public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
        {
            if (world.isRemote)
                return true;
            TestTileEntity tileEntity = (TestTileEntity) world.getTileEntity(pos);
            if (tileEntity == null)
            {
                return false;
            }
            tileEntity.isActive = !tileEntity.isActive;
            tileEntity.updateTicket();
            player.sendStatusMessage(new TextComponentString("Changed block powered state to " + tileEntity.isActive), true);
            logger.info("Changed block powered state at {} to {}", pos, tileEntity.isActive);
            return true;
        }

        @Override
        public void breakBlock(World world, BlockPos pos, IBlockState state)
        {
            if (world.isRemote)
                return;
            TestTileEntity tileEntity = (TestTileEntity) world.getTileEntity(pos);
            if (tileEntity == null)
                return;
            tileEntity.farmlandTicket.invalidate();
        }
    }

    public static class TestTileEntity extends TileEntity
    {
        private AABBTicket farmlandTicket;
        private boolean isActive = false;

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
            {
                farmlandTicket.validate();
            }
            else
            {
                farmlandTicket.invalidate();
            }
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound compound)
        {
            compound = super.writeToNBT(compound);
            compound.setBoolean("active", isActive);
            return compound;
        }

        @Override
        public void readFromNBT(NBTTagCompound compound)
        {
            super.readFromNBT(compound);
            isActive = compound.getBoolean("active");
        }

        @Override
        public void onChunkUnload()
        {
            if (!world.isRemote)
            {
                farmlandTicket.invalidate();
            }
        }
    }
}
