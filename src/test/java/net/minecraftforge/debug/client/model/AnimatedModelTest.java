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

package net.minecraftforge.debug.client.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
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
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(modid = AnimatedModelTest.MODID, name = "ForgeDebugModelAnimation", version = AnimatedModelTest.VERSION, acceptableRemoteVersions = "*")
public class AnimatedModelTest
{
    public static final String MODID = "forgedebugmodelanimation";
    public static final String VERSION = "0.0";

    public static final String blockName = "test_animation_block";
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    @ObjectHolder(blockName)
    public static final Block TEST_BLOCK = null;
    @ObjectHolder(blockName)
    public static final Item TEST_ITEM = null;

    public static final String rotateBlockName = "rotatest";
    @ObjectHolder(rotateBlockName)
    public static final Block TEST_ROTATE_BLOCK = null;
    @ObjectHolder(rotateBlockName)
    public static final Item TEST_ROTATE_ITEM = null;

    @Instance(MODID)
    public static AnimatedModelTest instance;

    public static CommonProxy proxy;
    private static Logger logger;


    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            GameRegistry.registerTileEntity(Chest.class, MODID + ":" + "tile_" + blockName);
            GameRegistry.registerTileEntity(Spin.class, MODID + ":" + "tile_" + rotateBlockName);
            event.getRegistry().register(
            new Block(Material.WOOD)
            {
                {
                    setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
                    setUnlocalizedName(MODID + "." + blockName);
                    setRegistryName(new ResourceLocation(MODID, blockName));
                }

                @Override
                public ExtendedBlockState createBlockState()
                {
                    return new ExtendedBlockState(this, new IProperty[]{FACING, Properties.StaticProperty}, new IUnlistedProperty[]{Properties.AnimationProperty});
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
                public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
                {
                    return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
                }

                @Override
                public IBlockState getStateFromMeta(int meta)
                {
                    return getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
                }

                @Override
                public int getMetaFromState(IBlockState state)
                {
                    return state.getValue(FACING).getIndex();
                }

                @Override
                public boolean hasTileEntity(IBlockState state)
                {
                    return true;
                }

                @Override
                public TileEntity createTileEntity(World world, IBlockState state)
                {
                    return new Chest();
                }

                @Override
                public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
                {
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
                public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
                {
                    if (world.isRemote)
                    {
                        TileEntity te = world.getTileEntity(pos);
                        if (te instanceof Chest)
                        {
                            ((Chest) te).click(player.isSneaking());
                        }
                    }
                    return true;
                }
            });

            event.getRegistry().register(new Block(Material.WOOD){
                {
                    setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
                    setUnlocalizedName(MODID + "." + rotateBlockName);
                    setRegistryName(new ResourceLocation(MODID, rotateBlockName));
                }

                @Override
                public ExtendedBlockState createBlockState()
                {
                    return new ExtendedBlockState(this, new IProperty[]{ Properties.StaticProperty }, new IUnlistedProperty[]{ Properties.AnimationProperty });
                }

                @Override
                public boolean isOpaqueCube(IBlockState state) { return false; }

                @Override
                public boolean isFullCube(IBlockState state) { return false; }

                @Override
                public boolean hasTileEntity(IBlockState state) {
                    return true;
                }

                @Override
                public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
                    return state.withProperty(Properties.StaticProperty, false);
                }

                @Override
                public int getMetaFromState(IBlockState state) {
                    return 0;
                }

                @Override
                public EnumBlockRenderType getRenderType(IBlockState state) {
                    return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
                }

                @Override
                public IBlockState getStateFromMeta(int meta) {
                    return getDefaultState();
                }

                @Override
                public TileEntity createTileEntity(World world, IBlockState state) {
                    return new Spin();
                }
            });
        }

        @net.minecraftforge.eventbus.api.SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(
            new ItemBlock(TEST_BLOCK)
            {
                @Override
                public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt)
                {
                    return new ItemAnimationHolder();
                }
            }.setRegistryName(TEST_BLOCK.getRegistryName())
            );
            event.getRegistry().register(
                    new ItemBlock(TEST_ROTATE_BLOCK) {

                    }.setRegistryName(TEST_ROTATE_BLOCK.getRegistryName())
            );
        }
    }

    public static abstract class CommonProxy
    {
        @Nullable
        public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters){ return null; };
    }

    public static class ServerProxy extends CommonProxy {}

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientProxy extends CommonProxy
    {

        public IAnimationStateMachine load(ResourceLocation location, ImmutableMap<String, ITimeValue> parameters)
        {
            return ModelLoaderRegistry.loadASM(location, parameters);
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            B3DLoader.INSTANCE.addDomain(MODID);
            ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation(TEST_ITEM.getRegistryName(), "inventory"));
            ClientRegistry.bindTileEntitySpecialRenderer(Chest.class, new AnimationTESR<Chest>()
            {
                @Override
                public void handleEvents(Chest chest, float time, Iterable<Event> pastEvents)
                {
                    chest.handleEvents(time, pastEvents);
                }
            });
            ModelLoader.setCustomModelResourceLocation(TEST_ROTATE_ITEM, 0, new ModelResourceLocation(TEST_ROTATE_ITEM.getRegistryName(), "inventory"));
            ClientRegistry.bindTileEntitySpecialRenderer(Spin.class, new AnimationTESR<Spin>());
            String entityName = MODID + ":entity_chest";
            //EntityRegistry.registerGlobalEntityID(EntityChest.class, entityName, EntityRegistry.findGlobalUniqueEntityId());
            EntityRegistry.registerModEntity(new ResourceLocation(entityName), EntityChest.class, entityName, 0, AnimatedModelTest.instance, 64, 20, true, 0xFFAAAA00, 0xFFDDDD00);
            RenderingRegistry.registerEntityRenderingHandler(EntityChest.class, new IRenderFactory<EntityChest>()
            {
                @SuppressWarnings("deprecation")
                public Render<EntityChest> createRenderFor(RenderManager manager)
                {
                /*model = ModelLoaderRegistry.getModel(new ResourceLocation(ModelLoaderRegistryTest.MODID, "block/chest.b3d"));
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
                            return TextureMap.LOCATION_BLOCKS_TEXTURE;
                        }
                    };
                }
            });
        }
    }

    private static class ItemAnimationHolder implements ICapabilityProvider
    {
        private final VariableValue cycleLength = new VariableValue(4);

        private final IAnimationStateMachine asm = proxy.load(new ResourceLocation(MODID.toLowerCase(), "asms/block/engine.json"), ImmutableMap.<String, ITimeValue>of(
                "cycle_length", cycleLength
        ));

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityAnimation.ANIMATION_CAPABILITY;
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return CapabilityAnimation.ANIMATION_CAPABILITY.cast(asm);
            }
            return null;
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }

    public static class Spin extends TileEntity
    {
        @Nullable
        private final IAnimationStateMachine asm;

        public Spin()
        {
            asm = proxy.load(new ResourceLocation(MODID, "asms/block/rotatest.json"), ImmutableMap.of());
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side)
        {
            return capability == CapabilityAnimation.ANIMATION_CAPABILITY || super.hasCapability(capability, side);
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side)
        {
            if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return CapabilityAnimation.ANIMATION_CAPABILITY.cast(asm);
            }
            return super.getCapability(capability, side);
        }

        @Override
        public boolean hasFastRenderer()
        {
            return true;
        }
    }

    public static class Chest extends TileEntity
    {
        @Nullable
        private final IAnimationStateMachine asm;
        private final VariableValue cycleLength = new VariableValue(4);
        private final VariableValue clickTime = new VariableValue(Float.NEGATIVE_INFINITY);
        //private final VariableValue offset = new VariableValue(0);

        public Chest()
        {
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
            for (Event event : pastEvents)
            {
                logger.info("Event: {} {} {} {}", event.event(), event.offset(), getPos(), time);
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
            if (asm != null)
            {
                if (sneaking)
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
                else if (asm.currentState().equals("default"))
                {
                    float time = Animation.getWorldTime(getWorld(), Animation.getPartialTickTime());
                    clickTime.setValue(time);
                    //offset.setValue(time);
                    //asm.transition("moving");
                    asm.transition("starting");
                }
                else if (asm.currentState().equals("moving"))
                {
                    clickTime.setValue(Animation.getWorldTime(getWorld(), Animation.getPartialTickTime()));
                    asm.transition("stopping");
                }
            }
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side)
        {
            if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return true;
            }
            return super.hasCapability(capability, side);
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side)
        {
            if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return CapabilityAnimation.ANIMATION_CAPABILITY.cast(asm);
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

        }

        @Override
        public void onEntityUpdate()
        {
            super.onEntityUpdate();
            if (world.isRemote && cycleLength != null)
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
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing side)
        {
            if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return true;
            }
            return super.hasCapability(capability, side);
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing side)
        {
            if (capability == CapabilityAnimation.ANIMATION_CAPABILITY)
            {
                return CapabilityAnimation.ANIMATION_CAPABILITY.cast(asm);
            }
            return super.getCapability(capability, side);
        }
    }
}

