package net.minecraftforge.debug;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.debug.ModelFluidDebug.TestFluid;
import net.minecraftforge.debug.ModelFluidDebug.TestGas;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import java.util.List;

@Mod(modid = "DynBucketTest", version = "0.1", dependencies = "after:" + ModelFluidDebug.MODID)
public class DynBucketTest
{
    public static final Item dynBucket = new DynBucket();
    public static final Item dynBottle = new DynBottle();

    static
    {
        FluidRegistry.enableUniversalBucket();
    }

    @SidedProxy
    public static CommonProxy proxy;

    public static class CommonProxy
    {
        void setupModels()
        {
        }
    }

    public static class ServerProxy extends CommonProxy
    {
    }

    public static class ClientProxy extends CommonProxy
    {
        @Override
        void setupModels()
        {
            ModelLoader.setBucketModelDefinition(dynBucket);

            final ModelResourceLocation bottle = new ModelResourceLocation(new ResourceLocation("forge", "dynbottle"), "inventory");
            ModelLoader.setCustomMeshDefinition(dynBottle, new ItemMeshDefinition()
            {
                @Override
                public ModelResourceLocation getModelLocation(ItemStack stack)
                {
                    return bottle;
                }
            });
            ModelBakery.registerItemVariants(dynBottle, bottle);
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerItem(new TestItem(), "testitem");
        GameRegistry.registerBlock(new BlockSimpleTank(), "simpletank");
        GameRegistry.registerTileEntity(TileSimpleTank.class, "simpletank");

        FluidRegistry.addBucketForFluid(FluidRegistry.getFluid(TestFluid.name));
        FluidRegistry.addBucketForFluid(FluidRegistry.getFluid(TestGas.name));

        //GameRegistry.registerItem(dynBucket, "dynbucket");
        GameRegistry.registerItem(dynBottle, "dynbottle");

        // register fluid containers
        int i = 0;
        //registerFluidContainer(FluidRegistry.WATER, i++);
        //registerFluidContainer(FluidRegistry.LAVA, i++);
        //registerFluidContainer(FluidRegistry.getFluid(TestFluid.name), i++);
        //registerFluidContainer(FluidRegistry.getFluid(TestGas.name), i++);

        i = 0;
        //registerFluidContainer2(FluidRegistry.WATER, i++);
        //registerFluidContainer2(FluidRegistry.LAVA, i++);
        //registerFluidContainer2(FluidRegistry.getFluid(TestFluid.name), i++);
        //registerFluidContainer2(FluidRegistry.getFluid(TestGas.name), i++);

        // Set TestFluidBlocks blockstate to use milk instead of testfluid for the texture to be loaded
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("milk"), new ItemStack(Items.milk_bucket), FluidContainerRegistry.EMPTY_BUCKET);

        proxy.setupModels();
        //MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerFluidContainer(Fluid fluid, int meta)
    {
        if (fluid == null)
            return;

        FluidStack fs = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
        ItemStack stack = new ItemStack(dynBucket, 1, meta);
        FluidContainerRegistry.registerFluidContainer(fs, stack, new ItemStack(Items.bucket));
    }

    private void registerFluidContainer2(Fluid fluid, int meta)
    {
        if (fluid == null)
            return;

        FluidStack fs = new FluidStack(fluid, 250);
        ItemStack stack = new ItemStack(dynBottle, 1, meta);
        FluidContainerRegistry.registerFluidContainer(fs, stack, new ItemStack(Items.glass_bottle));
    }

    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event)
    {
        IBlockState state = event.world.getBlockState(event.target.getBlockPos());
        if (state.getBlock() instanceof IFluidBlock)
        {
            Fluid fluid = ((IFluidBlock) state.getBlock()).getFluid();
            FluidStack fs = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);

            ItemStack filled = FluidContainerRegistry.fillFluidContainer(fs, event.current);
            if (filled != null)
            {
                event.result = filled;
                event.setResult(Result.ALLOW);
            }
        }
    }

    public static class TestItem extends Item {
        @Override
        public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn)
        {
            if(worldIn.isRemote)
                return itemStackIn;

            ItemStackHandler handler = new ItemStackHandler(5);
            ItemStackHandler handler2 = new ItemStackHandler(5);
            IItemHandler joined = new CombinedInvWrapper(handler, handler2);

            handler.setStackInSlot(0, new ItemStack(Blocks.stone));
            handler.setStackInSlot(1, new ItemStack(Blocks.grass));
            handler.setStackInSlot(2, new ItemStack(Blocks.dirt));
            handler.setStackInSlot(3, new ItemStack(Blocks.glass));
            handler.setStackInSlot(4, new ItemStack(Blocks.sand));

            handler2.setStackInSlot(0, new ItemStack(Blocks.slime_block));
            handler2.setStackInSlot(1, new ItemStack(Blocks.tnt));
            handler2.setStackInSlot(2, new ItemStack(Blocks.planks));
            handler2.setStackInSlot(3, new ItemStack(Blocks.log));
            handler2.setStackInSlot(4, new ItemStack(Blocks.diamond_block));

            for (int i = 0; i < handler.getSlots(); i++) {
                System.out.println("Expected 1: " + handler.getStackInSlot(i));
            }

            for (int i = 0; i < handler2.getSlots(); i++) {
                System.out.println("Expected 2: " + handler2.getStackInSlot(i));
            }

            for (int i = 0; i < joined.getSlots(); i++) {
                System.out.println("Joined: " + joined.getStackInSlot(i));
            }

            return itemStackIn;
        }
    }

    public static class DynBucket extends Item
    {
        public DynBucket()
        {
            setUnlocalizedName("dynbucket");
            setMaxStackSize(1);
            setHasSubtypes(true);
            setCreativeTab(CreativeTabs.tabMisc);
        }

        @Override
        public void getSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems)
        {
            for (int i = 0; i < 4; i++)
            {
                ItemStack bucket = new ItemStack(this, 1, i);
                if (FluidContainerRegistry.isFilledContainer(bucket))
                    subItems.add(bucket);
            }
        }
    }

    public static class DynBottle extends UniversalBucket
    {
        public DynBottle()
        {
            super(250, new ItemStack(Items.glass_bottle), true);
            setUnlocalizedName("dynbottle");
            setMaxStackSize(16);
            setHasSubtypes(true);
            setCreativeTab(CreativeTabs.tabMisc);
        }
    }

    // simple tank copied from tinkers construct
    public static class BlockSimpleTank extends BlockContainer
    {

        protected BlockSimpleTank()
        {
            super(Material.rock);
            setCreativeTab(CreativeTabs.tabMisc);
        }

        @Override
        public TileEntity createNewTileEntity(World worldIn, int meta)
        {
            return new TileSimpleTank();
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (!(te instanceof IFluidHandler))
            {
                return false;
            }
            IFluidHandler tank = (IFluidHandler) te;
            side = side.getOpposite();

            ItemStack stack = playerIn.getHeldItem();
            if (stack == null)
            {
                sendText(playerIn, tank, side);
                return false;
            }

            // do the thing with the tank and the buckets
            if (FluidUtil.interactWithTank(stack, playerIn, tank, side))
            {
                return true;
            }
            else
            {
                sendText(playerIn, tank, side);
            }

            // prevent interaction of the item if it's a fluidcontainer. Prevents placing liquids when interacting with the tank
            return FluidContainerRegistry.isFilledContainer(stack) || stack.getItem() instanceof IFluidContainerItem;
        }

        private void sendText(EntityPlayer player, IFluidHandler tank, EnumFacing side)
        {
            if (player.worldObj.isRemote)
            {
                String text;
                if (tank.getTankInfo(side).length > 0 && tank.getTankInfo(side)[0] != null && tank.getTankInfo(side)[0].fluid != null)
                {
                    text = tank.getTankInfo(side)[0].fluid.amount + "x " + tank.getTankInfo(side)[0].fluid.getLocalizedName();
                } else
                {
                    text = "empty";
                }
                player.addChatMessage(new ChatComponentText(text));
            }
        }
    }

    public static class TileSimpleTank extends TileEntity implements IFluidHandler
    {
        FluidTank tank = new FluidTank(4000);

        @Override
        public int fill(EnumFacing from, FluidStack resource, boolean doFill)
        {
            int filled = tank.fill(resource, doFill);
            if(doFill && filled > 0) {
                worldObj.markBlockForUpdate(pos);
            }
            return filled;
        }

        @Override
        public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain)
        {
            // not used in this test
            return null;
        }

        @Override
        public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain)
        {
            FluidStack drained = tank.drain(maxDrain, doDrain);
            if(doDrain && drained != null) {
                worldObj.markBlockForUpdate(pos);
            }
            return drained;
        }

        @Override
        public boolean canFill(EnumFacing from, Fluid fluid)
        {
            return tank.getFluidAmount() == 0 ||
                    (tank.getFluid().getFluid() == fluid && tank.getFluidAmount() < tank.getCapacity());
        }

        @Override
        public boolean canDrain(EnumFacing from, Fluid fluid)
        {
            return tank.getFluidAmount() > 0;
        }

        @Override
        public FluidTankInfo[] getTankInfo(EnumFacing from)
        {
            return new FluidTankInfo[]{new FluidTankInfo(tank)};
        }

        @Override
        public void readFromNBT(NBTTagCompound tags)
        {
            super.readFromNBT(tags);
            tank.readFromNBT(tags);
        }

        @Override
        public void writeToNBT(NBTTagCompound tags)
        {
            super.writeToNBT(tags);
            tank.writeToNBT(tags);
        }

        @Override
        public Packet getDescriptionPacket() {
            NBTTagCompound tag = new NBTTagCompound();
            writeToNBT(tag);
            return new S35PacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
        }

        @Override
        public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
            super.onDataPacket(net, pkt);
            readFromNBT(pkt.getNbtCompound());
        }
    }
}
