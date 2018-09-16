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

package net.minecraftforge.debug.client.rendering;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import static org.lwjgl.opengl.GL11.*;

@Mod(modid = ItemTESRTest.MODID, name = "ForgeDebugItemTile", version = "1.0", acceptableRemoteVersions = "*")
public class ItemTESRTest
{
    public static final String MODID = "forgedebugitemtile";
    @ObjectHolder(TestBlock.name)
    public static final Block TEST_BLOCK = null;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().register(new TestBlock());
            GameRegistry.registerTileEntity(CustomTileEntity.class, MODID.toLowerCase() + ":custom_tile_entity");
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new ItemBlock(TEST_BLOCK).setRegistryName(TEST_BLOCK.getRegistryName()));
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class BakeEventHandler
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            final ModelResourceLocation itemLocation = new ModelResourceLocation(TEST_BLOCK.getRegistryName(), "normal");

            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, TestBlock.name));
            ForgeHooksClient.registerTESRItemStack(item, 0, CustomTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(item, 0, itemLocation);
            ClientRegistry.bindTileEntitySpecialRenderer(CustomTileEntity.class, TestTESR.instance);
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void onModelBakeEvent(ModelBakeEvent event)
        {
            event.getModelManager().getBlockModelShapes().registerBuiltInBlocks(TEST_BLOCK);
        }

        public static class TestTESR extends TileEntitySpecialRenderer<CustomTileEntity>
        {
            private static final TestTESR instance = new TestTESR();

            private TestTESR()
            {
            }

            @Override
            public void render(CustomTileEntity p_180535_1_, double x, double y, double z, float p_180535_8_, int p_180535_9_, float partial)
            {
                glPushMatrix();
                glTranslated(x, y, z);
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                glColor4f(.2f, 1, .1f, 1);
                glBegin(GL_QUADS);
                glVertex3f(0, .5f, 0);
                glVertex3f(0, .5f, 1);
                glVertex3f(1, .5f, 1);
                glVertex3f(1, .5f, 0);
                glEnd();
                glPopMatrix();
                GlStateManager.enableTexture2D();
                GlStateManager.enableLighting();
            }
        }
    }

    public static class TestBlock extends BlockContainer
    {
        public static final String name = "custom_model_block";

        private TestBlock()
        {
            super(Material.IRON);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean causesSuffocation(IBlockState state)
        {
            return false;
        }

        @Override
        public TileEntity createNewTileEntity(World world, int meta)
        {
            return new CustomTileEntity();
        }
    }

    public static class CustomTileEntity extends TileEntity
    {
    }
}
