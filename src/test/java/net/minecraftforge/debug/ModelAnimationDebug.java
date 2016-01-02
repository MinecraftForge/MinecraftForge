package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.b3d.B3DLoader.B3DFrameProperty;
import net.minecraftforge.client.model.b3d.B3DLoader.B3DState;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.lwjgl.opengl.GL11;

@Mod(modid = ModelAnimationDebug.MODID, version = ModelAnimationDebug.VERSION)
public class ModelAnimationDebug
{
    public static final String MODID = "forgedebugmodelanimation";
    public static final String VERSION = "0.0";

    public static String blockName = "test_animation_block";
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool STATIC = PropertyBool.create("static");;

    @SidedProxy
    public static CommonProxy proxy;

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            GameRegistry.registerBlock(new Block(Material.wood)
            {
                {
                    setCreativeTab(CreativeTabs.tabBlock);
                    setUnlocalizedName(MODID + "." + blockName);
                }

                @Override
                public ExtendedBlockState createBlockState()
                {
                    return new ExtendedBlockState(this, new IProperty[]{ FACING, STATIC }, new IUnlistedProperty[]{ B3DFrameProperty.instance });
                }

                @Override
                public boolean isOpaqueCube() { return false; }

                @Override
                public boolean isFullCube() { return false; }

                @Override
                public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
                {
                    return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(world, pos, placer));
                }

                @Override
                public IBlockState getStateFromMeta(int meta) {
                    return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
                }

                @Override
                public int getMetaFromState(IBlockState state) {
                    return ((EnumFacing)state.getValue(FACING)).getIndex();
                }

                @Override
                public boolean hasTileEntity(IBlockState state) {
                    return true;
                }

                @Override
                public TileEntity createTileEntity(World world, IBlockState state) {
                    return new Chest(state);
                }

                @Override
                public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
                    return state.withProperty(STATIC, true);
                }

                /*@Override
                public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
                    TileEntity te = world.getTileEntity(pos);
                    if(te instanceof Chest && state instanceof IExtendedBlockState)
                    {
                        return ((Chest)te).getState((IExtendedBlockState)state);
                    }
                    return super.getExtendedState(state, world, pos);
                }*/

                @Override
                public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
                {
                    if(world.isRemote)
                    {
                        TileEntity te = world.getTileEntity(pos);
                        if(te instanceof Chest)
                        {
                            ((Chest)te).click();
                        }
                    }
                    return false;
                }
            }, blockName);
        }

        public void init(FMLInitializationEvent event) {}
    }

    public static class ServerProxy extends CommonProxy {}

    public static class ClientProxy extends CommonProxy
    {
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            B3DLoader.instance.addDomain(MODID);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(GameRegistry.findBlock(MODID, blockName)), 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + blockName, "inventory"));
            ClientRegistry.bindTileEntitySpecialRenderer(Chest.class, ChestRenderer.instance);
        }

        @Override
        public void init(FMLInitializationEvent event)
        {
            super.init(event);
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    @EventHandler
    public void init(FMLInitializationEvent event) { proxy.init(event); }

    private static class Chest extends TileEntity implements ITickable
    {
        private final int minFrame = 1;
        private final int maxFrame = 10;
        private int tick = minFrame;
        private boolean opening = false;
        private boolean closing = false;

        public Chest(IBlockState state) {
        }

        /*public IExtendedBlockState getState(IExtendedBlockState state) {
            return state.withProperty(B3DFrameProperty.instance, curState);
        }*/

        public void click()
        {
            if(opening || tick == maxFrame)
            {
                opening = false;
                closing = true;
                return;
            }
            if(closing || tick == minFrame)
            {
                closing = false;
                opening = true;
                return;
            }
            opening = true;
        }

        @Override
        public void update()
        {
            if(opening)
            {
                tick++;
                if(tick >= maxFrame)
                {
                    tick = maxFrame;
                    opening = false;
                }
            }
            if(closing)
            {
                tick--;
                if(tick <= minFrame)
                {
                    tick = minFrame;
                    closing = false;
                }
            }
        }

        public int getCurFrame()
        {
            return tick;
        }

        public int getNextFrame()
        {
            if(opening) return Math.min(tick + 1, maxFrame);
            if(closing) return Math.max(tick - 1, minFrame);
            return tick;
        }
    }

    private static class ChestRenderer extends TileEntitySpecialRenderer<Chest>
    {
        public static ChestRenderer instance = new ChestRenderer();
        private ChestRenderer() {}

        private BlockRendererDispatcher blockRenderer;

        public void renderTileEntityAt(Chest te, double x, double y, double z, float partialTick, int breakStage)
        {
            if(blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
            IBlockState state = te.getWorld().getBlockState(te.getPos());
            state = state.withProperty(STATIC, false);
            IBakedModel model = this.blockRenderer.getBlockModelShapes().getModelForState(state);
            if(state instanceof IExtendedBlockState)
            {
                IExtendedBlockState exState = (IExtendedBlockState)state;
                if(exState.getUnlistedNames().contains(B3DFrameProperty.instance))
                {
                    exState = exState.withProperty(B3DFrameProperty.instance, new B3DState(null, te.getCurFrame(), te.getNextFrame(), partialTick));
                    if(model instanceof ISmartBlockModel)
                    {
                        model = ((ISmartBlockModel)model).handleBlockState(exState);
                    }
                }
            }

            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            this.bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.enableBlend();
            GlStateManager.disableCull();

            if (Minecraft.isAmbientOcclusionEnabled())
            {
                GlStateManager.shadeModel(GL11.GL_SMOOTH);
            }
            else
            {
                GlStateManager.shadeModel(GL11.GL_FLAT);
            }

            worldrenderer.begin(7, DefaultVertexFormats.BLOCK);
            worldrenderer.setTranslation(x - te.getPos().getX(), y - te.getPos().getY(), z - te.getPos().getZ());

            this.blockRenderer.getBlockModelRenderer().renderModel(te.getWorld(), model, state, te.getPos(), worldrenderer, false);

            worldrenderer.setTranslation(0, 0, 0);
            tessellator.draw();

            RenderHelper.enableStandardItemLighting();
        }
    }
}

