package net.minecraftforge.debug;

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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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

@Mod(modid = DynBucketTest.MODID, version = "0.1", dependencies = "after:" + ModelFluidDebug.MODID)
public class DynBucketTest
{
    public static final String MODID = "DynBucketTest";
    public static final Item dynBucket = new DynBucket();
    public static final Item dynBottle = new DynBottle();
    private static final ResourceLocation simpleTankName = new ResourceLocation(MODID, "simpletank");
    private static final ResourceLocation testItemName = new ResourceLocation(MODID, "testitem");

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
            ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(simpleTankName), 0, new ModelResourceLocation(simpleTankName, "normal"));
            ModelLoader.setCustomModelResourceLocation(Item.REGISTRY.getObject(testItemName), 0, new ModelResourceLocation(new ResourceLocation("minecraft", "stick"), "inventory"));
        }
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.register(new TestItem(), testItemName);
        Block tank = new BlockSimpleTank();
        GameRegistry.register(tank, simpleTankName);
        GameRegistry.register(new ItemBlock(tank), simpleTankName);
        GameRegistry.registerTileEntity(TileSimpleTank.class, "simpletank");

        FluidRegistry.addBucketForFluid(FluidRegistry.getFluid(TestFluid.name));
        FluidRegistry.addBucketForFluid(FluidRegistry.getFluid(TestGas.name));

        //GameRegistry.registerItem(dynBucket, "dynbucket");
        GameRegistry.register(dynBottle);

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
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluid("milk"), new ItemStack(Items.MILK_BUCKET), FluidContainerRegistry.EMPTY_BUCKET);

        proxy.setupModels();
        //MinecraftForge.EVENT_BUS.register(this);
    }

    @SuppressWarnings("unused")
    private void registerFluidContainer(Fluid fluid, int meta)
    {
        if (fluid == null)
            return;

        FluidStack fs = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);
        ItemStack stack = new ItemStack(dynBucket, 1, meta);
        FluidContainerRegistry.registerFluidContainer(fs, stack, new ItemStack(Items.BUCKET));
    }

    @SuppressWarnings("unused")
    private void registerFluidContainer2(Fluid fluid, int meta)
    {
        if (fluid == null)
            return;

        FluidStack fs = new FluidStack(fluid, 250);
        ItemStack stack = new ItemStack(dynBottle, 1, meta);
        FluidContainerRegistry.registerFluidContainer(fs, stack, new ItemStack(Items.GLASS_BOTTLE));
    }

    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event)
    {
        IBlockState state = event.getWorld().getBlockState(event.getTarget().getBlockPos());
        if (state.getBlock() instanceof IFluidBlock)
        {
            Fluid fluid = ((IFluidBlock) state.getBlock()).getFluid();
            FluidStack fs = new FluidStack(fluid, FluidContainerRegistry.BUCKET_VOLUME);

            ItemStack filled = FluidContainerRegistry.fillFluidContainer(fs, event.getEmptyBucket());
            if (filled != null)
            {
                event.setFilledBucket(filled);
                event.setResult(Result.ALLOW);
            }
        }
    }

    public static class TestItem extends Item {
        @Override
        public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
        {
            if(worldIn.isRemote)
                return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStackIn);

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

            for (int i = 0; i < handler.getSlots(); i++) {
                System.out.println("Expected 1: " + handler.getStackInSlot(i));
            }

            for (int i = 0; i < handler2.getSlots(); i++) {
                System.out.println("Expected 2: " + handler2.getStackInSlot(i));
            }

            for (int i = 0; i < joined.getSlots(); i++) {
                System.out.println("Joined: " + joined.getStackInSlot(i));
            }

            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
        }
    }

    public static class DynBucket extends Item
    {
        public DynBucket()
        {
            setUnlocalizedName("dynbucket");
            setMaxStackSize(1);
            setHasSubtypes(true);
            setCreativeTab(CreativeTabs.MISC);
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
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            TileEntity te = worldIn.getTileEntity(pos);
            if (!(te instanceof IFluidHandler))
            {
                return false;
            }
            IFluidHandler tank = (IFluidHandler) te;
            side = side.getOpposite();

            if (heldItem == null)
            {
                sendText(playerIn, tank, side);
                return false;
            }

            // do the thing with the tank and the buckets
            if (FluidUtil.interactWithTank(heldItem, playerIn, tank, side))
            {
                return true;
            }
            else
            {
                sendText(playerIn, tank, side);
            }

            // prevent interaction of the item if it's a fluidcontainer. Prevents placing liquids when interacting with the tank
            return FluidContainerRegistry.isFilledContainer(heldItem) || heldItem.getItem() instanceof IFluidContainerItem;
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
                player.addChatMessage(new TextComponentString(text));
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
                IBlockState state = worldObj.getBlockState(pos);
                worldObj.notifyBlockUpdate(pos, state, state, 8); // TODO check flag
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
                IBlockState state = worldObj.getBlockState(pos);
                worldObj.notifyBlockUpdate(pos, state, state, 8); // TODO check flag
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
        public NBTTagCompound writeToNBT(NBTTagCompound tags)
        {
            tags = super.writeToNBT(tags);
            tank.writeToNBT(tags);
            return tags;
        }

        @Override
        public SPacketUpdateTileEntity getUpdatePacket() {
            NBTTagCompound tag = new NBTTagCompound();
            tag = writeToNBT(tag);
            return new SPacketUpdateTileEntity(this.getPos(), this.getBlockMetadata(), tag);
        }

        @Override
        public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
            super.onDataPacket(net, pkt);
            readFromNBT(pkt.getNbtCompound());
        }
    }
}
