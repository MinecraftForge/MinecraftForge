package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.gui.capability.CapabilityGuiProvider;
import net.minecraftforge.gui.capability.IGuiProvider;
import net.minecraftforge.gui.capability.impl.GuiProviderItem;
import net.minecraftforge.gui.capability.impl.GuiProviderTile;

import javax.annotation.Nullable;

@Mod(modid = "gui", name = "Entity Gui Capability Test")
public class TestGuiCapability
{
    @Mod.Instance("gui")
    public static TestGuiCapability INSTANCE;

    final static boolean ENABLE = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if(!ENABLE) return;
        FMLLog.info("Starting gui capability test.");
        TestBlock testBlock = new TestBlock();
        TestItem testItem = new TestItem();

        ItemBlock testItemBlock = new ItemBlock(testBlock);
        testItemBlock.setRegistryName(testBlock.getRegistryName());
        GameRegistry.register(testItemBlock);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        if(!ENABLE) return;
        GameRegistry.registerTileEntity(TileTestBlock.class, "test_tile");
    }

    private class TestBlock extends Block {

        public TestBlock()
        {
            super(Material.CIRCUITS);
            setCreativeTab(CreativeTabs.MISC);
            setRegistryName("gui", "testBlock");
            setUnlocalizedName("gui.test_block");
            GameRegistry.register(this);
        }

        @Override
        public boolean hasTileEntity(IBlockState state)
        {
            return true;
        }

        @Override
        public TileEntity createTileEntity(World world, IBlockState state)
        {
            return new TileTestBlock();
        }

        @Override
        public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand,
                @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
        {
            TileTestBlock tile = (TileTestBlock) worldIn.getTileEntity(pos);
            IGuiProvider cap = tile.getCapability(CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY, null);
            playerIn.openGui(cap);
            return true;
        }
    }

    private class TileTestBlock extends TileEntity
    {

        TestGuiProvider testGuiProvider;

        public TileTestBlock()
        {
            this.testGuiProvider = new TestGuiProvider(this, EnumFacing.UP);
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY || super.hasCapability(capability, facing);
        }

        @Override public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if(capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY) return (T) testGuiProvider;
            return super.getCapability(capability, facing);
        }

        private class TestGuiProvider extends GuiProviderTile
        {
            public TestGuiProvider(TileEntity tile, EnumFacing side)
            {
                super(tile, side);
            }

            @Override
            public Object getClientGuiElement(EntityPlayer player, World world, @Nullable Object owner)
            {
                return new GuiScreen()
                {
                    @Override public void drawScreen(int mouseX, int mouseY, float partialTicks)
                    {
                        super.drawScreen(mouseX, mouseY, partialTicks);
                        drawDefaultBackground();
                        fontRendererObj.drawString("Gui test", 10, 10, 0xFFFFFF);
                    }
                };
            }

            @Override
            public Object getServerGuiElement(EntityPlayer player, World world, @Nullable Object owner)
            {
                return null;
            }
        }
    }

    private class TestItem extends Item
    {
        public TestItem(){
            setRegistryName("gui", "item");
            setUnlocalizedName("gui.item");
            setCreativeTab(CreativeTabs.MISC);
            setMaxStackSize(1);
            GameRegistry.register(this);
        }

        @Override
        public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
        {
            if (itemStackIn.hasCapability(CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY, EnumFacing.UP))
            {
                IGuiProvider provider = itemStackIn.getCapability(CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY, hand == EnumHand.MAIN_HAND ? EnumFacing.UP : EnumFacing.DOWN);
                playerIn.openGui(provider);
                return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
            }

            return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);

        }

        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
        {
            return new ItemGuiHandler(stack);
        }
    }

    private class ItemGuiHandler implements ICapabilityProvider {

        private ItemStack stack;

        public ItemGuiHandler(ItemStack stack)
        {
            this.stack = stack;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
        {
            if(capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY) return (T) new TestItemGui(stack, facing == EnumFacing.UP ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND);
            return null;
        }

        private class TestItemGui extends GuiProviderItem
        {
            public TestItemGui(ItemStack stack, EnumHand hand)
            {
                super(stack, hand);
            }

            @Nullable
            @Override
            public Object getClientGuiElement(EntityPlayer player, World world, @Nullable Object owner)
            {
                return new GuiScreen()
                {
                    @Override
                    public void drawScreen(int mouseX, int mouseY, float partialTicks)
                    {
                        drawDefaultBackground();
                        super.drawScreen(mouseX, mouseY, partialTicks);
                        fontRendererObj.drawString("Item test", 10, 10, 0xFFFFFF);
                    }
                };
            }

            @Nullable
            @Override
            public Object getServerGuiElement(EntityPlayer player, World world, @Nullable Object owner)
            {
                return null;
            }
        }
    }
}
