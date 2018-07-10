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

package net.minecraftforge.debug.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.debug.client.model.ModelFluidTest;
import net.minecraftforge.debug.client.model.ModelFluidTest.TestFluid;
import net.minecraftforge.debug.client.model.ModelFluidTest.TestGas;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Mod(modid = DynBucketTest.MODID, name = "DynBucketTest", version = "0.1", dependencies = "after:" + ModelFluidTest.MODID, acceptableRemoteVersions = "*")
public class DynBucketTest
{
    public static final String MODID = "dynbuckettest";
    private static final ResourceLocation simpleTankName = new ResourceLocation(MODID, "simpletank");
    private static final ResourceLocation testItemName = new ResourceLocation(MODID, "testitem");

    private static final boolean ENABLE = false;
    private static Logger logger;

    @ObjectHolder("testitem")
    public static final Item TEST_ITEM = null;
    @ObjectHolder("simpletank")
    public static final Block TANK_BLOCK = null;
    @ObjectHolder("simpletank")
    public static final Item TANK_ITEM = null;
    @ObjectHolder("dynbottle")
    public static final Item DYN_BOTTLE = null;

    static
    {
        if (ENABLE && ModelFluidTest.ENABLE)
        {
            FluidRegistry.enableUniversalBucket();
        }
    }

    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event)
    {
        event.getRegistry().register(new BlockSimpleTank().setRegistryName(simpleTankName));
        GameRegistry.registerTileEntity(TileSimpleTank.class, "simpletank");
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event)
    {
        FluidRegistry.addBucketForFluid(FluidRegistry.getFluid(TestFluid.name));
        FluidRegistry.addBucketForFluid(FluidRegistry.getFluid(TestGas.name));

        event.getRegistry().registerAll(
            new TestItem().setRegistryName(testItemName),
            new ItemBlock(TANK_BLOCK).setRegistryName(simpleTankName),
            new DynBottle()
        );
    }

    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event)
    {
        ItemStack filledBucket = FluidUtil.getFilledBucket(new FluidStack(ModelFluidTest.FLUID, Fluid.BUCKET_VOLUME));
        GameRegistry.addShapelessRecipe(new ResourceLocation(MODID, "diamond_to_fluid"), null, filledBucket, Ingredient.fromItem(Items.DIAMOND));
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();

        if (ENABLE && ModelFluidTest.ENABLE)
        {
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event)
    {
        RayTraceResult target = event.getTarget();
        if (target != null)
        {
            IBlockState state = event.getWorld().getBlockState(target.getBlockPos());
            if (state.getBlock() instanceof IFluidBlock)
            {
                Fluid fluid = ((IFluidBlock) state.getBlock()).getFluid();
                FluidStack fs = new FluidStack(fluid, Fluid.BUCKET_VOLUME);

                ItemStack bucket = event.getEmptyBucket();
                IFluidHandlerItem fluidHandler = FluidUtil.getFluidHandler(bucket);
                if (fluidHandler != null)
                {
                    int fillAmount = fluidHandler.fill(fs, true);
                    if (fillAmount > 0)
                    {
                        ItemStack filledBucket = fluidHandler.getContainer();
                        event.setFilledBucket(filledBucket);
                        event.setResult(Result.ALLOW);
                    }
                }
            }
        }
    }

    @Mod.EventBusSubscriber(value = Side.CLIENT, modid = MODID)
    public static class ClientEventHandler
    {
        @SubscribeEvent
        public static void setupModels(ModelRegistryEvent event)
        {
            if (!ENABLE || !ModelFluidTest.ENABLE) return;

            ModelLoader.setBucketModelDefinition(DYN_BOTTLE);

            final ModelResourceLocation bottle = new ModelResourceLocation(new ResourceLocation(ForgeVersion.MOD_ID, "dynbottle"), "inventory");
            ModelLoader.setCustomMeshDefinition(DYN_BOTTLE, new ItemMeshDefinition()
            {
                @Override
                public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack)
                {
                    return bottle;
                }
            });
            ModelBakery.registerItemVariants(DYN_BOTTLE, bottle);
            ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(simpleTankName), 0, new ModelResourceLocation(simpleTankName, "normal"));
            ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(testItemName), 0, new ModelResourceLocation(new ResourceLocation("minecraft", "stick"), "inventory"));
        }
    }

    public static class TestItem extends Item
    {
        @Override
        public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
        {
            ItemStack itemStackIn = playerIn.getHeldItem(hand);
            if (worldIn.isRemote)
            {
                return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);
            }

            ItemStackHandler handler = new ItemStackHandler(5);
            ItemStackHandler handler2 = new ItemStackHandler(5);
            IItemHandler joined = new CombinedInvWrapper(handler, handler2);

            handler.setStackInSlot(0, new ItemStack(Blocks.STONE));
            handler.setStackInSlot(1, new ItemStack(Blocks.GRASS));
            handler.setStackInSlot(2, new ItemStack(Blocks.DIRT));
            handler.setStackInSlot(3, new ItemStack(Blocks.GLASS));
            handler.setStackInSlot(4, new ItemStack(Blocks.SAND));

            handler2.setStackInSlot(0, new ItemStack(Blocks.SLIME_BLOCK));
            handler2.setStackInSlot(1, new ItemStack(Blocks.TNT));
            handler2.setStackInSlot(2, new ItemStack(Blocks.PLANKS));
            handler2.setStackInSlot(3, new ItemStack(Blocks.LOG));
            handler2.setStackInSlot(4, new ItemStack(Blocks.DIAMOND_BLOCK));

            for (int i = 0; i < handler.getSlots(); i++)
            {
                logger.info("Expected 1: {}", handler.getStackInSlot(i));
            }

            for (int i = 0; i < handler2.getSlots(); i++)
            {
                logger.info("Expected 2: {}", handler2.getStackInSlot(i));
            }

            for (int i = 0; i < joined.getSlots(); i++)
            {
                logger.info("Joined: {}", joined.getStackInSlot(i));
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
        }
    }

    public static class DynBottle extends UniversalBucket
    {
        public DynBottle()
        {
            super(250, new ItemStack(Items.GLASS_BOTTLE), true);
            setUnlocalizedName("dynbottle");
            setRegistryName(new ResourceLocation(MODID, "dynbottle"));
            setMaxStackSize(16);
            setHasSubtypes(true);
            setCreativeTab(CreativeTabs.MISC);
        }
    }

    // simple tank copied from tinkers construct
    public static class BlockSimpleTank extends BlockContainer
    {

        protected BlockSimpleTank()
        {
            super(Material.ROCK);
            setCreativeTab(CreativeTabs.MISC);
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new TileSimpleTank();
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            IFluidHandler tank = FluidUtil.getFluidHandler(worldIn, pos, side.getOpposite());
            if (tank == null)
            {
                return false;
            }

            if (heldItem.isEmpty())
            {
                sendText(playerIn, tank);
                return false;
            }

            // do the thing with the tank and the buckets
            if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, side))
            {
                return true;
            }
            else
            {
                sendText(playerIn, tank);
            }

            // prevent interaction of the item if it's a fluidcontainer. Prevents placing liquids when interacting with the tank
            return FluidUtil.getFluidHandler(heldItem) != null;
        }

        private void sendText(EntityPlayer player, IFluidHandler tank)
        {
            if (player.world.isRemote)
            {
                String text;
                IFluidTankProperties[] tankProperties = tank.getTankProperties();
                if (tankProperties.length > 0 && tankProperties[0] != null && tankProperties[0].getContents() != null)
                {
                    text = tankProperties[0].getContents().amount + "x " + tankProperties[0].getContents().getLocalizedName();
                }
                else
                {
                    text = "empty";
                }
                player.sendMessage(new TextComponentString(text));
            }
        }
    }

    public static class TileSimpleTank extends TileEntity
    {
        FluidTank tank = new FluidTank(4000);

        @Override
        public void readFromNBT(NBTTagCompound tags)
        {
            super.readFromNBT(tags);
            tank.readFromNBT(tags);
        }

        @Override
        public NBTTagCompound writeToNBT(NBTTagCompound tags)
        {
            tags = super.writeToNBT(tags);
            tank.writeToNBT(tags);
            return tags;
        }

        @Override
        public SPacketUpdateTileEntity getUpdatePacket()
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag = writeToNBT(tag);
            return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
        }

        @Override
        public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt)
        {
            super.onDataPacket(net, pkt);
            readFromNBT(pkt.getNbtCompound());
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
        }

        @Override
        @Nullable
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing)
        {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            {
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
            }
            return super.getCapability(capability, facing);
        }
    }
}
