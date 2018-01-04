/*
 * Minecraft Forge
 * Copyright (c) 2016.
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
package net.minecraftforge.client.gui.tabs;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockChest.Type;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiDispenser;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;

public class DefaultVanillaGuiTabs
{
    public static final GuiTab VANILLA_INVENTORY_TAB = new GuiTab(null)
    {
        @Override
        public ItemStack getIconStack()
        {
            ItemStack skull = new ItemStack(Items.SKULL, 1, 3);
            skull.setTagCompound(new NBTTagCompound());
            NBTTagCompound compound = new NBTTagCompound();
            NBTUtil.writeGameProfile(compound, mc.getConnection().getGameProfile());
            skull.getTagCompound().setTag("SkullOwner", compound);
            return skull;
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            mc.displayGuiScreen(new GuiInventory(mc.player));
        }

        @Override
        public String getUnlocalizedName()
        {
            return "container.inventory";
        }
    }.addTo(GuiInventory.class).setTargetGui(GuiInventory.class);
    public static final GuiTab VANILLA_FURNACE_TAB = new GuiTab(null)
    {

        @Override
        public String getUnlocalizedName()
        {
            return "container.furnace";
        }

        @Override
        public ItemStack getIconStack()
        {
            return new ItemStack(Blocks.FURNACE);
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);
            if (tileEntity != null && tileEntity instanceof TileEntityFurnace)
            {
                mc.displayGuiScreen(new GuiFurnace(mc.player.inventory, ((TileEntityFurnace) tileEntity)));
            }
        }
    }.addTo(GuiFurnace.class).setTargetGui(GuiFurnace.class);
    public static final GuiTab VANILLA_WORKBENCH_TAB = new GuiTab(null)
    {
        @Override
        public String getUnlocalizedName()
        {
            return "container.crafting";
        }

        @Override
        public ItemStack getIconStack()
        {
            return new ItemStack(Blocks.CRAFTING_TABLE);
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            mc.displayGuiScreen(new GuiCrafting(mc.player.inventory, mc.world));
        }
    }.addTo(GuiCrafting.class).setTargetGui(GuiCrafting.class);
    public static final GuiTab VANILLA_BREWING_STAND_TAB = new GuiTab(null)
    {
        @Override public ResourceLocation getIconResLoc()
        {
            return new ResourceLocation("textures/items/brewing_stand.png");
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);
            if (tileEntity != null && tileEntity instanceof TileEntityBrewingStand)
            {
                mc.displayGuiScreen(new GuiBrewingStand(mc.player.inventory, ((TileEntityBrewingStand) tileEntity)));
            }
        }

        @Override
        public String getUnlocalizedName()
        {
            return "container.brewing";
        }
    }.addTo(GuiBrewingStand.class).setTargetGui(GuiBrewingStand.class);
    public static final GuiTab VANILLA_CHEST_TAB = new GuiTab(null)
    {
        @Override
        public ItemStack getIconStack()
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);

            if (tileEntity != null)
            {
                Block teBlock = tileEntity.getBlockType();
                if (teBlock instanceof BlockChest)
                {
                    if (((BlockChest) teBlock).chestType == Type.BASIC)
                    {
                        return new ItemStack(Blocks.CHEST);
                    }
                    else if (((BlockChest) teBlock).chestType == Type.TRAP)
                    {
                        return new ItemStack(Blocks.TRAPPED_CHEST);
                    }
                }
                else if (teBlock == Blocks.ENDER_CHEST)
                {
                    return new ItemStack(Blocks.ENDER_CHEST);
                }
            }
            return new ItemStack(Blocks.CHEST);
        }

        @Override
        public String getUnlocalizedName()
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);

            if (tileEntity != null)
            {
                Block teBlock = tileEntity.getBlockType();
                if (teBlock instanceof BlockChest)
                {
                    if (tileEntity instanceof TileEntityChest)
                    {
                        ((TileEntityChest) tileEntity).checkForAdjacentChests();
                        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                        {
                            if (mc.world.getBlockState(blockPos.offset(enumfacing)).getBlock() == Blocks.CHEST)
                            {
                                return "container.chestDouble";
                            }
                        }
                        return "container.chest";
                    }
                }
                else if (teBlock == Blocks.ENDER_CHEST)
                {
                    return "container.enderchest";
                }
            }
            return "container.chest";
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);

            if (tileEntity != null)
            {
                Block teBlock = tileEntity.getBlockType();
                if (teBlock instanceof BlockChest)
                {
                    if (tileEntity instanceof TileEntityChest)
                    {
                        ((TileEntityChest) tileEntity).checkForAdjacentChests();
                        ILockableContainer container = ((BlockChest) teBlock).getLockableContainer(mc.world, tileEntity.getPos());
                        if (container != null)
                        {
                            container.openInventory(mc.player);
                        }
                    }
                }
                else if (teBlock == Blocks.ENDER_CHEST)
                {
                    if (tileEntity instanceof TileEntityEnderChest)
                    {
                        InventoryEnderChest enderChest = new InventoryEnderChest();
                        enderChest.setChestTileEntity((TileEntityEnderChest) tileEntity);
                        enderChest.loadInventoryFromNBT(tileEntity.getTileData().getTagList("EnderItems", 10));
                        enderChest.openInventory(mc.player);
                    }
                }
            }
        }
    }.addTo(GuiChest.class).setTargetGui(GuiChest.class);
    public static final GuiTab VANILLA_BEACON_TAB = new GuiTab(null)
    {
        @Override
        public ItemStack getIconStack()
        {
            return new ItemStack(Blocks.BEACON);
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);
            if (tileEntity != null && tileEntity instanceof TileEntityBeacon)
            {
                mc.displayGuiScreen(new GuiBeacon(mc.player.inventory, (TileEntityBeacon) tileEntity));
            }
        }

        @Override
        public String getUnlocalizedName()
        {
            return "container.beacon";
        }
    }.addTo(GuiBeacon.class).setTargetGui(GuiBeacon.class);
    public static final GuiTab VANILLA_DISPENSER_TAB = new GuiTab(null)
    {
        @Override
        public ItemStack getIconStack()
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);

            if (tileEntity != null)
            {
                Block teBlock = tileEntity.getBlockType();
                if (teBlock == Blocks.DISPENSER)
                {
                    return new ItemStack(Blocks.DISPENSER);
                }
                else if (teBlock == Blocks.DROPPER)
                {
                    return new ItemStack(Blocks.DROPPER);
                }
            }
            return new ItemStack(Blocks.DISPENSER);
        }

        @Override
        public String getUnlocalizedName()
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);

            if (tileEntity != null)
            {
                Block teBlock = tileEntity.getBlockType();
                if (teBlock == Blocks.DISPENSER)
                {
                    return "container.dispenser";
                }
                else if (teBlock == Blocks.DROPPER)
                {
                    return "container.dropper";
                }
            }
            return "container.dispenser";
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);

            if (tileEntity != null)
            {
                if (tileEntity.getBlockType() == Blocks.CHEST)
                {
                    if (tileEntity instanceof TileEntityDispenser)
                    {
                        mc.displayGuiScreen(new GuiDispenser(mc.player.inventory, (TileEntityDispenser) tileEntity));
                    }
                }
                else if (tileEntity.getBlockType() == Blocks.ENDER_CHEST)
                {
                    if (tileEntity instanceof TileEntityDropper)
                    {
                        mc.displayGuiScreen(new GuiDispenser(mc.player.inventory, (TileEntityDropper) tileEntity));
                    }
                }
            }

        }
    }.addTo(GuiDispenser.class).setTargetGui(GuiDispenser.class);
    public static final GuiTab VANILLA_SHULKER_BOX_TAB = new GuiTab(null)
    {
        @Override
        public ItemStack getIconStack()
        {
            return new ItemStack(Blocks.PURPLE_SHULKER_BOX);
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);
            if (tileEntity != null && tileEntity instanceof TileEntityShulkerBox)
            {
                mc.displayGuiScreen(new GuiShulkerBox(mc.player.inventory, (TileEntityShulkerBox) tileEntity));
            }
        }

        @Override
        public String getUnlocalizedName()
        {
            return "container.shulkerBox";
        }
    }.addTo(GuiShulkerBox.class).setTargetGui(GuiShulkerBox.class);
    public static final GuiTab VANILLA_ENCHANTMENT_TABLE_TAB = new GuiTab(null)
    {
        @Override
        public ItemStack getIconStack()
        {
            return new ItemStack(Blocks.ENCHANTING_TABLE);
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            BlockPos blockPos = mc.objectMouseOver.getBlockPos();
            TileEntity tileEntity = mc.world.getTileEntity(blockPos);
            if (tileEntity != null && tileEntity instanceof TileEntityEnchantmentTable)
            {
                mc.displayGuiScreen(new GuiEnchantment(mc.player.inventory, mc.world, (TileEntityEnchantmentTable) tileEntity));
            }
        }

        @Override
        public String getUnlocalizedName()
        {
            return "container.enchant";
        }
    }.addTo(GuiEnchantment.class).setTargetGui(GuiEnchantment.class);
    public static final GuiTab VANILLA_ANVIL_TAB = new GuiTab(null)
    {
        @Override
        public ItemStack getIconStack()
        {
            return new ItemStack(Blocks.ANVIL);
        }

        @Override
        public void onTabClicked(GuiContainer guiContainer)
        {
            mc.displayGuiScreen(new GuiRepair(mc.player.inventory, mc.world));
        }

        @Override
        public String getUnlocalizedName()
        {
            return "container.repair";
        }
    }.addTo(GuiRepair.class).setTargetGui(GuiRepair.class);

}
