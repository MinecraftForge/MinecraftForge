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
import net.minecraft.block.BlockSlime;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMultiTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.api.distmarker.Dist;

@Mod(modid = StickyBlockTest.MODID, name = "ForgeDebugCustomSlimeBlock", version = StickyBlockTest.VERSION, acceptableRemoteVersions = "*")
public class StickyBlockTest
{
    public static final String MODID = "forgedebugcustomslimeblock";
    public static final String ASSETS = "forgedebugcustomslimeblock:";
    public static final String VERSION = "1.0";
    public static Block CUSTOM_SLIME_BLOCK;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        CUSTOM_SLIME_BLOCK = new CustomSlime();
        ForgeRegistries.BLOCKS.register(CUSTOM_SLIME_BLOCK);
        ForgeRegistries.ITEMS.register(new ItemMultiTexture(CUSTOM_SLIME_BLOCK, CUSTOM_SLIME_BLOCK, stack -> CustomSlime.BlockType.values[stack.getMetadata()].toString()).setRegistryName(CUSTOM_SLIME_BLOCK.getRegistryName()));
    }

    @EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class BakeEventHandler
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            Item item = Item.getItemFromBlock(CUSTOM_SLIME_BLOCK);
            ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(ASSETS + "blue_slime_block", "inventory"));
            ModelLoader.setCustomModelResourceLocation(item, 1, new ModelResourceLocation(ASSETS + "obsidian_slime_block", "inventory"));
        }
    }

    public static class CustomSlime extends BlockSlime
    {
        private static final PropertyEnum<BlockType> VARIANT = PropertyEnum.create("variant", BlockType.class);

        private CustomSlime()
        {
            super();
            this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockType.BLUE_SLIME_BLOCK));
            this.setCreativeTab(CreativeTabs.DECORATIONS);
            this.setUnlocalizedName("custom_slime_block");
            this.setRegistryName("forgedebugcustomslimeblock:custom_slime_block");
            this.setSoundType(SoundType.SLIME);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public void getSubBlocks(CreativeTabs creativeTabs, NonNullList<ItemStack> list)
        {
            for (int i = 0; i < BlockType.values.length; ++i)
            {
                list.add(new ItemStack(this, 1, i));
            }
        }

        @Override
        protected BlockStateContainer createBlockState()
        {
            return new BlockStateContainer(this, VARIANT);
        }

        @Override
        public IBlockState getStateFromMeta(int meta)
        {
            return this.getDefaultState().withProperty(VARIANT, BlockType.values[meta]);
        }

        @Override
        public int getMetaFromState(IBlockState state)
        {
            return state.getValue(VARIANT).ordinal();
        }

        @Override
        public boolean isStickyBlock(IBlockState state)
        {
            return state.getValue(VARIANT) != BlockType.OBSIDIAN_SLIME_BLOCK;
        }

        public static enum BlockType implements IStringSerializable
        {
            BLUE_SLIME_BLOCK,
            OBSIDIAN_SLIME_BLOCK;

            protected static final BlockType[] values = BlockType.values();

            @Override
            public String toString()
            {
                return this.name().toLowerCase();
            }

            @Override
            public String getName()
            {
                return this.name().toLowerCase();
            }
        }
    }
}