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

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.client.model.animation.FastTESR;
import net.minecraftforge.client.render.BatchedBufferConfig;
import net.minecraftforge.client.render.BatchedTileEntityRenderer;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.render.IBufferDrawer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

@Mod(modid = FastTESRCustomFormatTest.MODID, name = "CustomVertexFormatFastTESR", version = "1.0", acceptableRemoteVersions = "*")
@Mod.EventBusSubscriber
public class FastTESRCustomFormatTest
{

    static final String MODID = "custom_vertex_format_fast_tesr";

    private static class CustomFormatFastTESR extends FastTESR<CustomFormatTE> implements IBufferDrawer
    {
        public static final CustomFormatFastTESR INSTANCE = new CustomFormatFastTESR();
        public static final BatchedBufferConfig CONFIG = new BatchedBufferConfig(DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, INSTANCE);

        private CustomFormatFastTESR()
        {
            super(CONFIG);
        }

        @Override
        public void renderTileEntityFast(CustomFormatTE te, double x, double y, double z, float partialTicks, int destroyStage, float partial, Map<BatchedBufferConfig, Tessellator> map)
        {
            BufferBuilder builder = map.get(CONFIG).getBuffer();
            builder.pos(x + 1, y + 1, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y + 1, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y, z).color(255, 0, 0, 255).endVertex();
            builder.pos(x + 1, y, z).color(255, 0, 0, 255).endVertex();

            builder.pos(x, y, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x + 1D, y, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x + 1D, y, z + 0.5D).color(255, 0, 0, 255).endVertex();
            builder.pos(x, y, z + 0.5D).color(255, 0, 0, 255).endVertex();

            z += 0.5D;
            builder.pos(x + 1D, y, z + 0.5D).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y, z + 0.5D).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y, z).color(255, 0, 0, 255).endVertex();
            builder.pos(x + 1D, y, z).color(255, 0, 0, 255).endVertex();
            z -= 0.5D;

            builder.pos(x, y + 1, z + 1).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y + 1, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y, z).color(255, 0, 0, 255).endVertex();
            builder.pos(x, y, z + 1).color(255, 0, 0, 255).endVertex();

            z++;
            builder.pos(x + 1, y + 1, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y + 1, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y, z).color(255, 0, 0, 255).endVertex();
            builder.pos(x + 1, y, z).color(255, 0, 0, 255).endVertex();

            z--;
            y++;
            builder.pos(x, y, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x + 1D, y, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x + 1D, y, z + 0.5D).color(255, 0, 0, 255).endVertex();
            builder.pos(x, y, z + 0.5D).color(255, 0, 0, 255).endVertex();

            z += 0.5D;
            builder.pos(x + 1D, y, z + 0.5D).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y, z + 0.5D).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y, z).color(255, 0, 0, 255).endVertex();
            builder.pos(x + 1D, y, z).color(255, 0, 0, 255).endVertex();
            z -= 0.5D;

            y--;
            x++;
            builder.pos(x, y + 1, z + 1).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y + 1, z).color(255, 255, 255, 255).endVertex();
            builder.pos(x, y, z).color(255, 0, 0, 255).endVertex();
            builder.pos(x, y, z + 1).color(255, 0, 0, 255).endVertex();
            x--;
        }

        @Override
        public void preDraw(int renderPass, BufferBuilder builder)
        {
            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableTexture2D();
            GlStateManager.shadeModel(GL11.GL_SMOOTH);
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 0, 240);
        }

        @Override
        public void postDraw(int renderPass)
        {
            RenderHelper.enableStandardItemLighting();
            GlStateManager.shadeModel(GL11.GL_FLAT);
            GlStateManager.enableTexture2D();
        }
    }

    public static class CustomFormatTE extends TileEntity
    {

        public CustomFormatTE()
        {
        }

        @Override
        public boolean hasFastRenderer()
        {
            return true;
        }
    }


    private static final Block testBlock = new BlockContainer(Material.ROCK)
    {

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new CustomFormatTE();
        }

        @Override
        public EnumBlockRenderType getRenderType(IBlockState state)
        {
            return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
        }
    };

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent evt)
    {
        GameRegistry.registerTileEntity(CustomFormatTE.class, new ResourceLocation(MODID, "fast-tesr-te"));
    }

    @SubscribeEvent
    public static void onBlockRegister(RegistryEvent.Register<Block> evt)
    {
        evt.getRegistry().register(testBlock.setCreativeTab(CreativeTabs.DECORATIONS).setRegistryName(MODID, "fluid-tesr-block"));
    }

    @SubscribeEvent
    public static void onItemRegister(RegistryEvent.Register<Item> evt)
    {
        evt.getRegistry().register(new ItemBlock(testBlock).setRegistryName(MODID, "tesr-test"));
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientLoader
    {
        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelBakery.registerItemVariants(Item.getItemFromBlock(testBlock));
            ClientRegistry.bindTileEntitySpecialRenderer(CustomFormatTE.class, new CustomFormatFastTESR());
            BatchedTileEntityRenderer.registerBatchedVertexFormat(CustomFormatFastTESR.CONFIG);
        }
    }
}
