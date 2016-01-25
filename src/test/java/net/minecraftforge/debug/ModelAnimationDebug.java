package net.minecraftforge.debug;

import java.io.IOException;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IModelState;
import net.minecraftforge.client.model.IRetexturableModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.MultiModel;
import net.minecraftforge.client.model.TRSRTransformation;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.AnimationModelBase;
import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.client.model.animation.Event;
import net.minecraftforge.client.model.animation.IAnimationProvider;
import net.minecraftforge.client.model.animation.ITimeValue;
import net.minecraftforge.client.model.animation.TimeValues.VariableValue;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.common.model.animation.IAnimationStateMachine;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;
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

import com.google.common.collect.ImmutableMap;

@Mod(modid = ModelAnimationDebug.MODID, version = ModelAnimationDebug.VERSION)
public class ModelAnimationDebug
{
    public static final String MODID = "forgedebugmodelanimation";
    public static final String VERSION = "0.0";

    public static String blockName = "test_animation_block";
    public static final PropertyDirection FACING = PropertyDirection.create("facing");

    @Instance(MODID)
    public static ModelAnimationDebug instance;

    @SidedProxy
    public static CommonProxy proxy;

    public static abstract class CommonProxy
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
                    return new ExtendedBlockState(this, new IProperty[]{ FACING, Properties.StaticProperty }, new IUnlistedProperty[]{ Properties.AnimationProperty });
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
                    return new Chest();
                }

                @Override
                public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
                    return state.withProperty(Properties.StaticProperty, true);
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

        public abstract IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters);
    }

    public static class ServerProxy extends CommonProxy
    {
        public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters)
        {
            return null;
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
            ClientRegistry.bindTileEntitySpecialRenderer(Chest.class, new AnimationTESR<Chest>()
            {
                @Override
                public void handleEvents(Chest chest, float time, Iterable<Event> pastEvents)
                {
                    chest.handleEvents(time, pastEvents);
                }
            });
            String entityName = MODID + ":entity_chest";
            //EntityRegistry.registerGlobalEntityID(EntityChest.class, entityName, EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerModEntity(EntityChest.class, entityName, 0, ModelAnimationDebug.instance, 64, 20, true, 0xFFAAAA00, 0xFFDDDD00);
            RenderingRegistry.registerEntityRenderingHandler(EntityChest.class, new IRenderFactory<EntityChest>()
            {
                public Render<EntityChest> createRenderFor(RenderManager manager)
                {
                    try
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
                        IModel model = new MultiModel(
                            new ResourceLocation(ModelAnimationDebug.MODID, "builtin/engine"),
                            ring,
                            TRSRTransformation.identity(),
                            ImmutableMap.of(
                                "base", Pair.<IModel, IModelState>of(base, TRSRTransformation.identity())
                            )
                        );
                        return new RenderLiving<EntityChest>(manager, new AnimationModelBase<EntityChest>(model, new VertexLighterSmoothAo())
                            {
                                @Override
                                public void handleEvents(EntityChest chest, float time, Iterable<Event> pastEvents)
                                {
                                    chest.handleEvents(time, pastEvents);
                                }
                            }, 0.5f)
                        {
                            protected ResourceLocation getEntityTexture(EntityChest entity)
                            {
                                return TextureMap.locationBlocksTexture;
                            }
                        };
                    }
                    catch(IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });
        }

        public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters)
        {
            return Animation.INSTANCE.load(location, parameters);
        }

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    public static class Chest extends TileEntity implements IAnimationProvider
    {
        private final IAnimationStateMachine asm;
        private final VariableValue cycleLength = new VariableValue(4);
        private final VariableValue clickTime = new VariableValue(Float.NEGATIVE_INFINITY);
        //private final VariableValue offset = new VariableValue(0);

        public Chest() {
            /*asm = proxy.load(new ResourceLocation(MODID.toLowerCase(), "asms/block/chest.json"), ImmutableMap.<String, ITimeValue>of(
                "click_time", clickTime
            ));*/
            asm = proxy.load(new ResourceLocation(MODID.toLowerCase(), "asms/block/engine.json"), ImmutableMap.<String, ITimeValue>of(
                "cycle_length", cycleLength,
                "click_time", clickTime
                //"offset", offset
            ));
        }

        public void handleEvents(float time, Iterable<Event> pastEvents)
        {
            for(Event event : pastEvents)
            {
                System.out.println("Event: " + event.event() + " " + event.offset() + " " + getPos() + " " + time);
            }
        }

        @Override
        public boolean hasFastRenderer()
        {
            return true;
        }

        /*public IExtendedBlockState getState(IExtendedBlockState state) {
            return state.withProperty(B3DFrameProperty.instance, curState);
        }*/

        public void click(boolean sneaking)
        {
            if(asm != null)
            {
                if(sneaking)
                {
                    cycleLength.setValue(6 - cycleLength.apply(0));
                }
                /*else if(asm.currentState().equals("closed"))
                {
                    clickTime.setValue(Animation.getWorldTime(getWorld()));
                    asm.transition("opening");
                }
                else if(asm.currentState().equals("open"))
                {
                    clickTime.setValue(Animation.getWorldTime(getWorld()));
                    asm.transition("closing");
                }*/
                else if(asm.currentState().equals("default"))
                {
                    float time = Animation.getWorldTime(getWorld(), Animation.getPartialTickTime());
                    clickTime.setValue(time);
                    //offset.setValue(time);
                    //asm.transition("moving");
                    asm.transition("starting");
                }
                else if(asm.currentState().equals("moving"))
                {
                    clickTime.setValue(Animation.getWorldTime(getWorld(), Animation.getPartialTickTime()));
                    asm.transition("stopping");
                }
            }
        }

        public IAnimationStateMachine asm()
        {
            return asm;
        }
    }

    public static class EntityChest extends EntityLiving implements IAnimationProvider
    {
        private final IAnimationStateMachine asm;
        private VariableValue cycleLength;

        public EntityChest(World world)
        {
            super(world);
            setSize(1, 1);
            if(cycleLength == null)
            {
                cycleLength = new VariableValue(getHealth() / 5);
            }
            asm = proxy.load(new ResourceLocation(MODID.toLowerCase(), "asms/block/engine.json"), ImmutableMap.<String, ITimeValue>of(
                "cycle_length", cycleLength
            ));
        }

        public void handleEvents(float time, Iterable<Event> pastEvents)
        {
            // TODO Auto-generated method stub
        }

        public IAnimationStateMachine asm()
        {
            return asm;
        }

        @Override
        public void onDataWatcherUpdate(int id)
        {
            super.onDataWatcherUpdate(id);
            if(id == 6) // health
            {
                if(cycleLength == null)
                {
                    cycleLength = new VariableValue(0);
                }
                cycleLength.setValue(getHealth() / 5);
            }
        }

        @Override
        protected void applyEntityAttributes()
        {
            super.applyEntityAttributes();
            this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(60);
        }
    }
}

