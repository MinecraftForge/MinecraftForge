package net.minecraftforge.test;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GuiRegistry;
import net.minecraftforge.gui.IGuiProvider;
import net.minecraftforge.gui.impl.GuiProviderEntity;
import net.minecraftforge.gui.impl.GuiProviderItem;
import net.minecraftforge.gui.impl.GuiProviderTile;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

import javax.annotation.Nullable;
import java.awt.*;

@Mod(modid = "gui", name = "Gui Data Test")
public class TestGuis
{
    @Mod.Instance("gui")
    public static TestGuis INSTANCE;

    final static boolean ENABLE = true;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if(!ENABLE) return;
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
    public void entityHit(AttackEntityEvent event)
    {
        EntityPlayer source = event.getEntityPlayer();
        if (event.getTarget() instanceof EntityPig)
        {
            IGuiProvider gui = new EntityHealthGui(event.getTarget());
            source.openGui(gui);
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
            IGuiProvider cap = new TileTestBlock.TestGuiProvider(tile, side);
            playerIn.openGui(cap, hand);
            return true;
        }
    }

    public static class TileTestBlock extends TileEntity
    {

        public TileTestBlock() { }

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
            fontRendererObj.drawString("Gui test", 10, 10, Color.ORANGE.darker().getRGB());
            GlStateManager.popMatrix();
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
        {
            drawDefaultBackground();
            drawRect(guiLeft, guiTop, guiLeft + xSize, guiTop + ySize, Color.lightGray.getRGB());
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
            IGuiProvider provider = new TestItemGui(itemStackIn);
            playerIn.openGui(provider, hand);
            return ActionResult.newResult(EnumActionResult.SUCCESS, itemStackIn);
        }
    }

    private class TestItemGui extends GuiProviderItem
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
    }

    private class EntityHealthGui extends GuiProviderEntity
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
                        fontRendererObj.drawString(String.format("<%s> Ow!", owner.getName()), 10, 10, 0xFF0000, true);
                        if(owner instanceof EntityLiving)
                        {
                            EntityLiving ownerLiving = (EntityLiving) owner;
                            fontRendererObj.drawString("Health remaining: " + ownerLiving.getHealth(), 10, 10 + fontRendererObj.FONT_HEIGHT, java.awt.Color.CYAN.getRGB());
                        }

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
    }
}
