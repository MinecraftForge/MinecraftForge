package net.minecraftforge.debug;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelCustomData;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ISmartBlockModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.AnimationStateMachine;
import net.minecraftforge.client.model.animation.IAnimationProvider;
import net.minecraftforge.client.model.animation.IParameter;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.pipeline.VertexLighterFlat;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.client.model.pipeline.WorldRendererConsumer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

@Mod(modid = ModelAnimationDebug.MODID, version = ModelAnimationDebug.VERSION)
public class ModelAnimationDebug
{
    public static final String MODID = "forgedebugmodelanimation";
    public static final String VERSION = "0.0";

    public static String blockName = "test_animation_block";
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool STATIC = PropertyBool.create("static");;

    @Instance(MODID)
    public static ModelAnimationDebug instance;

    @SidedProxy(serverSide = "net.minecraftforge.debug.ModelAnimationDebug$CommonProxy", clientSide = "net.minecraftforge.debug.ModelAnimationDebug$ClientProxy")
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
                    return new ExtendedBlockState(this, new IProperty[]{ FACING, STATIC }, new IUnlistedProperty[]{ Animation.AnimationProperty });
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
                            ((Chest)te).click(player.isSneaking());
                        }
                    }
                    return true;
                }
            }, blockName);
            GameRegistry.registerTileEntity(Chest.class, MODID + ":" + "tile_" + blockName);
        }
    }

    public static class ClientProxy extends CommonProxy
    {
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            B3DLoader.instance.addDomain(MODID);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(GameRegistry.findBlock(MODID, blockName)), 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + blockName, "inventory"));
            ClientRegistry.bindTileEntitySpecialRenderer(Chest.class, ChestRenderer.instance);
            String entityName = MODID + ":entity_chest";
            //EntityRegistry.registerGlobalEntityID(EntityChest.class, entityName, EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerModEntity(EntityChest.class, entityName, 0, ModelAnimationDebug.instance, 64, 20, true, 0xFFAAAA00, 0xFFDDDD00);
            RenderingRegistry.registerEntityRenderingHandler(EntityChest.class, new IRenderFactory<EntityChest>()
            {
                public RenderChest createRenderFor(RenderManager manager)
                {
                    return new RenderChest(manager, new ChestModel(), 0.5f);
                }
            });
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    private static abstract class CycleParameter implements IParameter
    {
        protected abstract float getCycleLength();

        @Override
        public int hashCode()
        {
            return Objects.hashCode(getCycleLength());
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CycleParameter other = (CycleParameter) obj;
            return getCycleLength() == other.getCycleLength();
        }
    }

    private static abstract class WorldToCycle extends CycleParameter
    {
        public float apply(float input)
        {
            return input / getCycleLength();
        }
    }

    private static abstract class RoundCycle extends CycleParameter
    {
        public float apply(float input)
        {
            float l = getCycleLength();
            return (float)Math.ceil(input / l) * l - input;
        }
    }

    private static class Chest extends TileEntity implements IAnimationProvider
    {
        private final AnimationStateMachine asm;
        private float cycleLength = 4;

        public Chest(IBlockState state) {
            asm = Animation.load(new ResourceLocation("forgedebugmodelanimation", "afsm/block/engine"), ImmutableMap.<String, IParameter>of(
                "worldToCycle", new WorldToCycle()
                {
                    protected float getCycleLength()
                    {
                        return Chest.this.cycleLength;
                    }
                },
                "roundCycle", new RoundCycle()
                {
                    protected float getCycleLength()
                    {
                        return Chest.this.cycleLength;
                    }
                }
            ));
        }

        /*public IExtendedBlockState getState(IExtendedBlockState state) {
            return state.withProperty(B3DFrameProperty.instance, curState);
        }*/

        public void click(boolean sneaking)
        {
            if(sneaking)
            {
                cycleLength = 6 - cycleLength;
            }
            else if(!asm.transitioning())
            {
                if(asm.currentState().equals("default"))
                {
                    asm.transition(Animation.getWorldTime(getWorld()), "moving");
                }
                else if(asm.currentState().equals("moving"))
                {
                    asm.transition(Animation.getWorldTime(getWorld()), "default");
                }
            }
        }

        public AnimationStateMachine asm()
        {
            return asm;
        }
    }

    private static class ChestRenderer extends TileEntitySpecialRenderer<Chest>
    {
        private final LoadingCache<Pair<IExtendedBlockState, IModelState>, IBakedModel> modelCache = CacheBuilder.newBuilder().maximumSize(10).expireAfterWrite(100, TimeUnit.MILLISECONDS).build(new CacheLoader<Pair<IExtendedBlockState, IModelState>, IBakedModel>()
        {
            public IBakedModel load(Pair<IExtendedBlockState, IModelState> key) throws Exception
            {
                IBakedModel model = blockRenderer.getBlockModelShapes().getModelForState(key.getLeft().getClean());
                if(model instanceof ISmartBlockModel)
                {
                    model = ((ISmartBlockModel)model).handleBlockState(key.getLeft().withProperty(Animation.AnimationProperty, key.getRight()));
                }
                return model;
            }
        });

        public static ChestRenderer instance = new ChestRenderer();
        private ChestRenderer() {}

        private BlockRendererDispatcher blockRenderer;

        private IBakedModel getModel(IExtendedBlockState state, IModelState modelState)
        {
            return modelCache.getUnchecked(Pair.of(state, modelState));
        }

        public void renderTileEntityAt(Chest te, double x, double y, double z, float partialTick, int breakStage)
        {
            if(blockRenderer == null) blockRenderer = Minecraft.getMinecraft().getBlockRendererDispatcher();
            BlockPos pos = te.getPos();
            IBlockAccess world = MinecraftForgeClient.getRegionRenderCache(te.getWorld(), pos);
            IBlockState state = world.getBlockState(pos);
            if(state.getPropertyNames().contains(STATIC))
            {
                state = state.withProperty(STATIC, false);
            }
            if(state instanceof IExtendedBlockState)
            {
                IExtendedBlockState exState = (IExtendedBlockState)state;
                if(exState.getUnlistedNames().contains(Animation.AnimationProperty))
                {
                    IBakedModel model = getModel(exState, te.asm().apply(Animation.getWorldTime(getWorld(), partialTick)));

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
                    worldrenderer.setTranslation(x - pos.getX(), y - pos.getY(), z - pos.getZ());

                    blockRenderer.getBlockModelRenderer().renderModel(world, model, state, pos, worldrenderer, false);

                    worldrenderer.setTranslation(0, 0, 0);
                    tessellator.draw();

                    RenderHelper.enableStandardItemLighting();
                }
            }
        }
    }

    public static class EntityChest extends EntityLiving implements IAnimationProvider
    {
        private final AnimationStateMachine asm;

        public EntityChest(World world)
        {
            super(world);
            setSize(1, 1);
            asm = Animation.load(new ResourceLocation("forgedebugmodelanimation", "afsm/block/engine"), ImmutableMap.<String, IParameter>of(
                "worldToCycle", new WorldToCycle()
                {
                    protected float getCycleLength()
                    {
                        return EntityChest.this.getHealth() / 5;
                    }
                },
                "roundCycle", new RoundCycle()
                {
                    protected float getCycleLength()
                    {
                        return EntityChest.this.getHealth() / 5;
                    }
                }
            ));
        }

        public AnimationStateMachine asm()
        {
            return asm;
        }

        @Override
        protected void applyEntityAttributes()
        {
            super.applyEntityAttributes();
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60);
        }
    }

    public static class RenderChest extends RenderLiving<EntityChest>
    {
        public RenderChest(RenderManager manager, ModelBase model, float shadowSize)
        {
            super(manager, model, shadowSize);
        }

        @Override
        protected ResourceLocation getEntityTexture(EntityChest entity)
        {
            return TextureMap.locationBlocksTexture;
        }
    }

    public static class ChestModel extends ModelBase
    {
        private final VertexLighterFlat lighter = new VertexLighterSmoothAo();

        private IModel model;

        @Override
        public void render(Entity entity, float limbSwing, float limbSwingSpeed, float timeAlive, float yawHead, float rotationPitch, float scale)
        {
            if(model == null) try
            {
                /*model = ModelLoaderRegistry.getModel(new ResourceLocation(ModelLoaderRegistryDebug.MODID, "block/chest.b3d"));
                if(model instanceof IRetexturableModel)
                {
                    model = ((IRetexturableModel)model).retexture(ImmutableMap.of("#chest", "entity/chest/normal"));
                }
                if(model instanceof IModelCustomData)
                {
                    model = ((IModelCustomData)model).process(ImmutableMap.of("mesh", "[\"Base\", \"Lid\"]"));
                }*/
                IModel base = ModelLoaderRegistry.getModel(new ResourceLocation(ModelAnimationDebug.MODID, "block/engine"));
                IModel ring = ModelLoaderRegistry.getModel(new ResourceLocation(ModelAnimationDebug.MODID, "block/engine_ring"));
                ImmutableMap<String, String> textures = ImmutableMap.of(
                    "base", "blocks/stone",
                    "front", "blocks/log_oak",
                    "chamber", "blocks/redstone_block",
                    "trunk", "blocks/end_stone"
                );
                if(base instanceof IRetexturableModel)
                {
                    base = ((IRetexturableModel)base).retexture(textures);
                }
                if(ring instanceof IRetexturableModel)
                {
                    ring = ((IRetexturableModel)ring).retexture(textures);
                }
                model = new MultiModel(
                    new ResourceLocation(ModelAnimationDebug.MODID, "builtin/engine"),
                    ring,
                    TRSRTransformation.identity(),
                    ImmutableMap.of(
                        "base", Pair.of(base, TRSRTransformation.identity())
                    )
                );
            }
            catch(IOException e)
            {
                Throwables.propagate(e);
            }

            IBakedModel bakedModel = model.bake(((IAnimationProvider)entity).asm().apply(timeAlive / 20), DefaultVertexFormats.ITEM, new Function<ResourceLocation, TextureAtlasSprite>()
            {
                public TextureAtlasSprite apply(ResourceLocation location)
                {
                    return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
                }
            });

            BlockPos pos = new BlockPos(entity.posX, entity.posY + entity.height, entity.posZ);

            RenderHelper.disableStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180, 0, 0, 1);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
            worldRenderer.setTranslation(-0.5, -1.5, -0.5);

            lighter.setParent(new WorldRendererConsumer(worldRenderer));
            lighter.setWorld(entity.worldObj);
            lighter.setBlock(GameRegistry.findBlock(MODID, blockName));
            lighter.setBlockPos(pos);
            boolean empty = true;
            List<BakedQuad> quads = bakedModel.getGeneralQuads();
            if(!quads.isEmpty())
            {
                lighter.updateBlockInfo();
                empty = false;
                for(BakedQuad quad : quads)
                {
                    quad.pipe(lighter);
                }
            }
            for(EnumFacing side : EnumFacing.values())
            {
                quads = bakedModel.getFaceQuads(side);
                if(!quads.isEmpty())
                {
                    if(empty) lighter.updateBlockInfo();
                    empty = false;
                    for(BakedQuad quad : quads)
                    {
                        quad.pipe(lighter);
                    }
                }
            }

            /*worldRenderer.pos(0, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 0).lightmap(240, 0).endVertex();
            worldRenderer.pos(0, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(0, 1).lightmap(240, 0).endVertex();
            worldRenderer.pos(1, 1, 1).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 1).lightmap(240, 0).endVertex();
            worldRenderer.pos(1, 1, 0).color(0xFF, 0xFF, 0xFF, 0xFF).tex(1, 0).lightmap(240, 0).endVertex();*/

            worldRenderer.setTranslation(0, 0, 0);

            tessellator.draw();
            GlStateManager.popMatrix();
            RenderHelper.enableStandardItemLighting();
        }
    }
}

