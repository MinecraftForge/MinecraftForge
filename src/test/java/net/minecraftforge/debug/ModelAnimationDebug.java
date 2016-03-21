package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.animation.Animation;
import net.minecraftforge.client.model.animation.AnimationTESR;
import net.minecraftforge.client.model.b3d.B3DLoader;
import net.minecraftforge.client.model.pipeline.VertexLighterSmoothAo;
import net.minecraftforge.common.animation.Event;
import net.minecraftforge.common.animation.ITimeValue;
import net.minecraftforge.common.animation.TimeValues.VariableValue;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.model.animation.CapabilityAnimation;
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
                public boolean isOpaqueCube(IBlockState state) { return false; }

                @Override
                public boolean isFullCube(IBlockState state) { return false; }

                @Override
                public IBlockState onBlockPlaced(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
                {
                    return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(pos, placer));
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
                public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
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
            }, null, blockName);
            GameRegistry.registerItem(new ItemBlock(GameRegistry.findBlock(MODID, blockName))
            {
                @Override
                public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
                {
                    return new ItemAnimationHolder();
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
                @SuppressWarnings("deprecation")
                public Render<EntityChest> createRenderFor(RenderManager manager)
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
                    ResourceLocation location = new ModelResourceLocation(new ResourceLocation(MODID, blockName), "entity");
                    return new RenderLiving<EntityChest>(manager, new net.minecraftforge.client.model.animation.AnimationModelBase<EntityChest>(location, new VertexLighterSmoothAo(Minecraft.getMinecraft().getBlockColors()))
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
            });
        }

        public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters)
        {
            return ModelLoaderRegistry.loadASM(location, parameters);
        }

    }

    private static class ItemAnimationHolder implements ICapabilityProvider
    {
        private final VariableValue cycleLength = new VariableValue(4);

        private final IAnimationStateMachine asm = proxy.load(new ResourceLocation(MODID.toLowerCase(), "asms/block/engine.json"), ImmutableMap.<String, ITimeValue>of(
            "cycle_length", cycleLength
        ));

        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return capability == CapabilityAnimation.ANIMATION_CAPABILITY;
        }

        @SuppressWarnings("unchecked")
        public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if(capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return (T)asm;
            }
            return null;
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }

    public static class Chest extends TileEntity
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

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing side)
        {
            if(capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return true;
            }
            return super.hasCapability(capability, side);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing side)
        {
            if(capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return (T)asm;
            }
            return super.getCapability(capability, side);
        }
    }

    public static class EntityChest extends EntityLiving
    {
        private final IAnimationStateMachine asm;
        private final VariableValue cycleLength = new VariableValue(getHealth() / 5);

        public EntityChest(World world)
        {
            super(world);
            setSize(1, 1);
            asm = proxy.load(new ResourceLocation(MODID.toLowerCase(), "asms/block/engine.json"), ImmutableMap.<String, ITimeValue>of(
                "cycle_length", cycleLength
            ));
        }

        public void handleEvents(float time, Iterable<Event> pastEvents)
        {
            // TODO Auto-generated method stub
        }

        @Override
        public void onEntityUpdate()
        {
            super.onEntityUpdate();
            if(worldObj.isRemote && cycleLength != null)
            {
                cycleLength.setValue(getHealth() / 5);
            }
        }

        @Override
        protected void applyEntityAttributes()
        {
            super.applyEntityAttributes();
            this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(60);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing side)
        {
            if(capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return true;
            }
            return super.hasCapability(capability, side);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing side)
        {
            if(capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return (T)asm;
            }
            return super.getCapability(capability, side);
        }
    }
}

