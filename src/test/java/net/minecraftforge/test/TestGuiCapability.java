package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.datafix.fixes.EntityHealth;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GuiRegistry;
import net.minecraftforge.gui.capability.CapabilityGuiProvider;
import net.minecraftforge.gui.capability.IGuiProvider;
import net.minecraftforge.gui.capability.impl.GuiProviderEntity;
import net.minecraftforge.gui.capability.impl.GuiProviderItem;
import net.minecraftforge.gui.capability.impl.GuiProviderTile;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

import javax.annotation.Nullable;

@Mod(modid = "gui", name = "Gui Capability Test")
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

        GuiRegistry.register(new TestItemGui());
        GuiRegistry.register(new TileTestBlock.TestGuiProvider());
        GuiRegistry.register(new EntityHealthGui());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event){
        if(!ENABLE) return;
        GameRegistry.registerTileEntity(TileTestBlock.class, "test_tile");
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void attach(AttachCapabilitiesEvent.Entity entity)
    {
        if(entity.getEntity() instanceof EntityPig)
        {
            ICapabilityProvider newInstance = new EntityHealthGui(entity.getEntity());
            entity.addCapability(new ResourceLocation("gui", "guiTest"), newInstance);
        }
    }

    @SubscribeEvent
    public void entityHit(AttackEntityEvent event)
    {
        EntityPlayer source = event.getEntityPlayer();
        if (event.getTarget() instanceof EntityPig)
        {
            if(event.getTarget().hasCapability(CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY, null))
            {
                IGuiProvider gui = event.getTarget().getCapability(CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY, null);
                source.openGui(gui);
            }
        }
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
            playerIn.openGui(cap, side, hand);
            return true;
        }
    }

    public static class TileTestBlock extends TileEntity
    {

        private TestGuiProvider gui = new TestGuiProvider(this);

        public TileTestBlock() {

            FMLLog.info(pos.toString());
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing)
        {
            return capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY || super.hasCapability(capability, facing);
        }

        @Override public <T> T getCapability(Capability<T> capability, EnumFacing facing)
        {
            if(capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY) return (T) gui;
            return super.getCapability(capability, facing);
        }

        public static class TestGuiProvider extends GuiProviderTile
        {

            public TestGuiProvider() { }
            public TestGuiProvider(TileEntity in)
            {
                super(in, null);
            }

            public TestGuiProvider(TileEntity in, EnumFacing side)
            {
                super(in, side);
            }

            @Override
            public GuiScreen clientElement(World world, EntityPlayer player)
            {
                return new GuiTileTest(player, (TileTestBlock) owner);
            }

            @Override
            public Container serverElement(World world, EntityPlayer player)
            {
                return new ContainerTileTest(player, (TileTestBlock) owner);
            }

            @Override
            public ResourceLocation getGuiIdentifier() {
                return new ResourceLocation("forgetest:tile_gui");
            }
        }
    }

    private static class GuiTileTest extends GuiContainer
    {
        public GuiTileTest(EntityPlayer player, TileTestBlock tile)
        {
            super(new ContainerTileTest(player, tile));
            xSize = 400;
            ySize = 300;
        }

        @Override
        protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
        {
            super.drawGuiContainerForegroundLayer(mouseX, mouseY);
            GlStateManager.pushMatrix();
            GlStateManager.translate(guiLeft, guiTop, 0);
            fontRendererObj.drawString("Gui test", 10, 10, 0xFFFFFF);
            GlStateManager.popMatrix();
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
        {
            drawDefaultBackground();
        }
    }

    private static class ContainerTileTest extends Container
    {
        public ContainerTileTest(EntityPlayer player, TileTestBlock tile)
        {
            PlayerInvWrapper wrapper = new PlayerInvWrapper(player.inventory);
            for(int i = 0; i < 9; ++i){
                SlotItemHandler s = new SlotItemHandler(wrapper, i, 10 + (i * 18), 50);
                this.addSlotToContainer(s);
            }
        }

        @Override
        public boolean canInteractWith(EntityPlayer playerIn)
        {
            return true;
        }

        @Nullable
        @Override
        public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
        {
            return null;
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
                IGuiProvider provider = itemStackIn.getCapability(CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY, null);
                playerIn.openGui(provider, hand);
                return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
            }

            return ActionResult.newResult(EnumActionResult.PASS, itemStackIn);

        }

        @Override
        public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
        {
            return new TestItemGui(stack);
        }
    }

    private class TestItemGui extends GuiProviderItem implements ICapabilityProvider
    {

        public TestItemGui() { }
        public TestItemGui(ItemStack in)
        {
            super(in);
        }

        @Override
        public GuiScreen clientElement(World world, EntityPlayer player)
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
        public Container serverElement(World world, EntityPlayer player) {
            return null;
        }

        @Override
        public ResourceLocation getGuiIdentifier()
        {
            return new ResourceLocation("forgetest:item_gui");
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
            return capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
            if(capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY) return (T) new TestItemGui(owner);
            return null;
        }
    }

    private class EntityHealthGui extends GuiProviderEntity implements ICapabilityProvider
    {

        public EntityHealthGui() { }
        public EntityHealthGui(Entity in)
        {
            super(in);
        }

        @Override
        public GuiScreen clientElement(World world, EntityPlayer player)
        {
            return new GuiScreen()
            {
                @Override
                public void drawScreen(int mouseX, int mouseY, float partialTicks)
                {
                    drawDefaultBackground();
                    super.drawScreen(mouseX, mouseY, partialTicks);
                    if(owner != null)
                    {
                        fontRendererObj.drawString(owner.getName(), 10, 10, 0xFF0000, true);
                        fontRendererObj.drawString(" says OW!", fontRendererObj.getStringWidth(owner.getName()) + 10, 10, 0xFFFFFF);
                    }
                }
            };
        }

        @Nullable
        @Override
        public Container serverElement(World world, EntityPlayer player)
        {
            return null;
        }

        @Override
        public ResourceLocation getGuiIdentifier() {
            return new ResourceLocation("forgetest:entity_gui");
        }

        @Override
        public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
        {
            return capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
        {
            if(capability == CapabilityGuiProvider.GUI_PROVIDER_CAPABILITY) return (T) new EntityHealthGui(owner);
            return null;
        }
    }
}
