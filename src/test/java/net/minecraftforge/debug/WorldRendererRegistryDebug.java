package net.minecraftforge.debug;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.RenderGlobal.ContainerLocalRenderInformation;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IWorldRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.WorldRendererRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.lwjgl.BufferUtils;

import com.google.common.collect.MapMaker;

@Mod(modid = WorldRendererRegistryDebug.MODID, version = WorldRendererRegistryDebug.VERSION)
public class WorldRendererRegistryDebug
{
    public static final String MODID = "ForgeDebugWorldRendererRegistry";
    public static final String VERSION = "1.0";

    public static final String DebugBlockName = "debug_block";
    public static Block getDebugBlock()
    {
        return GameData.getBlockRegistry().getObject(MODID.toLowerCase() + ":" + DebugBlockName);
    }

    @SidedProxy(serverSide = "net.minecraftforge.debug.WorldRendererRegistryDebug$CommonProxy", clientSide = "net.minecraftforge.debug.WorldRendererRegistryDebug$ClientProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLInitializationEvent event) { proxy.init(event); }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) { proxy.postInit(event); }

    public static class CommonProxy
    {
        public void init(FMLInitializationEvent event)
        {
            GameRegistry.registerBlock(new Block(Material.iron){
                @Override public int     getRenderType()    { return MinecraftForgeClient.CUSTOM_WORLD_RENDERER_ID; }
                @Override public boolean isOpaqueCube()     { return false; }
                @Override public boolean isFullCube()       { return false; }
                @Override public boolean isVisuallyOpaque() { return false; }
            }.setCreativeTab(CreativeTabs.tabBlock).setUnlocalizedName(DebugBlockName), DebugBlockName);
        }

        public void postInit(FMLPostInitializationEvent event) {}
    }

    public static class ClientProxy extends CommonProxy
    {
        @Override
        public void init(FMLInitializationEvent event)
        {
            super.init(event);
            WorldRendererRegistry.registerRenderer(DebugWorldRenderer.instance, getDebugBlock());
        }

        @Override
        public void postInit(FMLPostInitializationEvent event) {
            super.postInit(event);
            Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().registerBuiltInBlocks(getDebugBlock());
            Item item = Item.getItemFromBlock(getDebugBlock());
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + DebugBlockName, "inventory"));
        }
    }

    public static enum DebugWorldRenderer implements IWorldRenderer
    {
        instance;

        private static final VertexFormat format = new VertexFormat();
        private static final int BUFFER_SIZE;

        static
        {
            format.setElement(new VertexFormatElement(0, VertexFormatElement.EnumType.INT, VertexFormatElement.EnumUsage.POSITION, 3));
            BUFFER_SIZE = 16 * 16 * 16 * format.getNextOffset();
        }

        private static class VBO
        {
            private final int id;
            private int size = 0;

            public VBO()
            {
                this.id = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, id);
                glBufferData(GL_ARRAY_BUFFER, BUFFER_SIZE, GL_DYNAMIC_DRAW);
                cleanupSet.add(new VBOWeak(this));
            }

            public void upload(ByteBuffer buffer)
            {
                synchronized(buffer)
                {
                    buffer.flip();
                    size = buffer.remaining();
                    glBindBuffer(GL_ARRAY_BUFFER, id);
                    glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
                }
            }

            public void draw()
            {
                glBindBuffer(GL_ARRAY_BUFFER, id);
                glVertexPointer(3, GL_INT, format.getNextOffset(), 0);
                glDrawArrays(GL_POINTS, 0, size);
            }

            private static ReferenceQueue<VBO> vboQueue = new ReferenceQueue<VBO>();
            private static Set<VBOWeak> cleanupSet = new HashSet<VBOWeak>();

            private static class VBOWeak extends WeakReference<VBO>
            {
                private final int id;

                public VBOWeak(VBO buffer)
                {
                    super(buffer, vboQueue);
                    this.id = buffer.id;
                }

                public void clean()
                {
                    glDeleteBuffers(id);
                }
            }

            public static void cleanup()
            {
                for(Reference<? extends VBO> ref = vboQueue.poll(); ref != null; ref = vboQueue.poll())
                {
                    if(ref instanceof VBOWeak)
                    {
                        ((VBOWeak)ref).clean();
                        cleanupSet.remove(ref);
                    }
                }
            }
        }

        ConcurrentMap<RenderChunk, ByteBuffer> buffers = new MapMaker().weakKeys().makeMap();
        ConcurrentMap<RenderChunk, VBO> vbos = new MapMaker().weakKeys().makeMap();

        private ByteBuffer getBuffer(RenderChunk renderChunk)
        {
            if(!buffers.containsKey(renderChunk))
            {
                buffers.putIfAbsent(renderChunk, BufferUtils.createByteBuffer(BUFFER_SIZE));
            }
            return buffers.get(renderChunk);
        }

        private VBO getVbo(RenderChunk renderChunk)
        {
            if(!vbos.containsKey(renderChunk))
            {
                vbos.putIfAbsent(renderChunk, new VBO());
            }
            return vbos.get(renderChunk);
        }

        Queue<RenderChunk> uploads = new ConcurrentLinkedQueue<RenderChunk>();

        public void onPreRenderLayer(RenderGlobal renderer, List<ContainerLocalRenderInformation> renderInfos, ChunkRenderContainer chunkContainer, EnumWorldBlockLayer layer, double partialTicks, int pass, Entity entity)
        {
        }

        public void onPostRenderLayer(RenderGlobal renderer, List<ContainerLocalRenderInformation> renderInfos, ChunkRenderContainer chunkContainer, EnumWorldBlockLayer layer, double partialTicks, int pass, Entity entity)
        {
            if(layer == EnumWorldBlockLayer.SOLID)
            {
                VBO.cleanup();
                RenderChunk chunk;
                while((chunk = uploads.poll()) != null)
                {
                    getVbo(chunk).upload(getBuffer(chunk));
                }

                glPointSize(20);
                glColor4f(1, 0, 1, 1);
                glEnableClientState(GL_VERTEX_ARRAY);
                glPushMatrix();
                glTranslated(
                    -entity.lastTickPosX - (entity.posX - entity.lastTickPosX) * partialTicks,
                    -entity.lastTickPosY - (entity.posY - entity.lastTickPosY) * partialTicks,
                    -entity.lastTickPosZ - (entity.posZ - entity.lastTickPosZ) * partialTicks);
                glTranslatef(.5f, .5f, .5f);

                for(ContainerLocalRenderInformation info : renderInfos)
                {
                    glPushMatrix();
                    BlockPos pos = info.renderChunk.getPosition();

                    // data is in absolute coordinates, so this is not needed
                    //glTranslatef(pos.getX(), pos.getY(), pos.getZ());

                    getVbo(info.renderChunk).draw();

                    glPopMatrix();
                }
                glBindBuffer(GL_ARRAY_BUFFER, 0);
                glPopMatrix();
                glDisableClientState(GL_VERTEX_ARRAY);
            }
        }

        public void onPreRebuildChunk(RenderChunk renderChunk, EnumWorldBlockLayer layer, BlockPos pos)
        {
            if(layer == EnumWorldBlockLayer.SOLID)
            {
                ByteBuffer buf = getBuffer(renderChunk);
                synchronized(buf)
                {
                    buf.clear();
                }
            }
        }

        public boolean onBlockRender(RenderChunk renderChunk, IBlockState state, IBlockAccess world, BlockPos pos)
        {
            if(state.getBlock() == getDebugBlock())
            {
                ByteBuffer buf = getBuffer(renderChunk);
                synchronized(buf)
                {
                    buf.putInt(pos.getX()).putInt(pos.getY()).putInt(pos.getZ());
                }
                return true;
            }
            return false;
        }

        public void onPostRebuildChunk(RenderChunk renderChunk, EnumWorldBlockLayer layer, BlockPos pos)
        {
            if(layer == EnumWorldBlockLayer.SOLID)
            {
                uploads.add(renderChunk);
            }
        }

        @Override
        public String toString()
        {
            return this.getClass() + "#instance";
        }
    }
}
