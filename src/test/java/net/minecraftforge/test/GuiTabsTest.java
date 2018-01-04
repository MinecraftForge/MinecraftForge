package net.minecraftforge.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.gui.tabs.GuiTab;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import io.netty.buffer.ByteBuf;

/**
 * Adds 25 tabs to the player inventory and 13 to a test gui Also checks if item stack icon and ResourceLocation icons are working
 */
@Mod(name = "GuiTabsTest", modid = "guitabtest", version = "0.0.0.0")
@EventBusSubscriber
public class GuiTabsTest
{

    public static final boolean ENABLE = false;

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("guitabtest");

    @Instance
    public static GuiTabsTest instance;

    @SidedProxy
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        if (!ENABLE)
        {
            return;
        }
        proxy.init();
    }

    @SubscribeEvent
    public static void onBlckRightCLick(ItemTossEvent e)
    {
        if (!ENABLE)
        {
            return;
        }
        if (e.getEntityItem().getItem().getItem() == Items.DIAMOND)
        {
            Minecraft.getMinecraft().displayGuiScreen(new TestGuiContainer(new TestContainer()));
            new GuiTab("Tab1", new ItemStack(Blocks.BEDROCK), GuiChest.class)
            {
                @Override
                public void onTabClicked(GuiContainer guiContainer)
                {
                    super.onTabClicked(guiContainer);
                    GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(0));
                }
            }.addTo(TestGuiContainer.class);
        }
    }

    public static class CommonProxy
    {
        public void init()
        {
            if (!ENABLE)
            {
                return;
            }
            NetworkRegistry.INSTANCE.registerGuiHandler(GuiTabsTest.instance, new GuiHandler());
            GuiTabsTest.INSTANCE.registerMessage(TabMessageHandler.class, TabMessageHandler.TabMessage.class, 0, Side.SERVER);
        }
    }

    public static class ServerProxy extends CommonProxy
    {
        public void init()
        {
            if (!ENABLE)
            {
                return;
            }
            super.init();
        }
    }

    public static class ClientProxy extends CommonProxy
    {
        public void init()
        {
            super.init();
            if (!ENABLE)
            {
                return;
            }
            new GuiTab("Tab1", new ItemStack(Blocks.BEDROCK), TestGui.class)
            {
                @Override
                public void onTabClicked(GuiContainer guiContainer)
                {
                    super.onTabClicked(guiContainer);
                    GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(0));
                }
            }.addTo(GuiInventory.class);
            new GuiTab("Tab1", new ItemStack(Blocks.BEDROCK), TestGui.class)
            {
                @Override
                public void onTabClicked(GuiContainer guiContainer)
                {
                    super.onTabClicked(guiContainer);
                    GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(0));
                }
            }.addTo(GuiChest.class);
            ItemStack is = new ItemStack(Items.APPLE);
            is.addEnchantment(Enchantments.PROTECTION, 1);
            new GuiTab("Tab3", is, TestGui2.class)
            {
                @Override
                public void onTabClicked(GuiContainer guiContainer)
                {
                    super.onTabClicked(guiContainer);
                    GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(1));
                }
            }.addTo(GuiInventory.class);
            for (int i = 0; i < 12; i++)
            {
                new GuiTab("Tab" + (2 + i), new ItemStack(Blocks.ENCHANTING_TABLE), GuiEnchantment.class)
                {
                    @Override
                    public void onTabClicked(GuiContainer guiContainer)
                    {
                        super.onTabClicked(guiContainer);
                        GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(2));
                    }
                }.addTo(GuiCrafting.class);
            }
            for (int i = 0; i < 12; i++)
            {
                new GuiTab("Tab" + (2 + i), new ItemStack(Blocks.ENCHANTING_TABLE), GuiEnchantment.class)
                {
                    @Override
                    public void onTabClicked(GuiContainer guiContainer)
                    {
                        super.onTabClicked(guiContainer);
                        GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(1));
                    }
                }.addTo(GuiInventory.class);
            }
        }
    }

    public static class TestGuiContainer extends GuiContainer
    {

        public TestGuiContainer(Container inventorySlotsIn)
        {
            super(inventorySlotsIn);
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
        {
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks)
        {
            drawDefaultBackground();
        }
    }

    public static class TestContainer extends Container
    {

        @Override
        public boolean canInteractWith(EntityPlayer playerIn)
        {
            return true;
        }

    }

    public static class TestGui extends GuiChest
    {
        public TestGui(IInventory upperInv, IInventory lowerInv)
        {
            super(upperInv, lowerInv);
        }
    }

    public static class TestGui2 extends GuiFurnace
    {
        public TestGui2(InventoryPlayer playerInv, IInventory furnaceInv)
        {
            super(playerInv, furnaceInv);
        }
    }

    public static class GuiHandler implements IGuiHandler
    {

        IInventory invCChest1, invSChest1, invCChest2, invSChest2;

        IInventory invCFurnace, invSFurnace;

        @Override
        public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
        {
            InventoryPlayer inventory = player.inventory;
            if (invSChest1 == null)
            {
                invSChest1 = new InventoryBasic("Test Chest 1", true, 27);
            }
            if (invSFurnace == null)
            {
                invSFurnace = new InventoryBasic("Test Furnace", true, 3);
            }
            if (invSChest2 == null)
            {
                invSChest2 = new InventoryBasic("Test Chest 2", true, 27);
            }
            switch (ID)
            {
            case 0:
                return new ContainerChest(inventory, invSChest1, player);
            case 1:
                return new ContainerFurnace(inventory, invSFurnace);
            case 2:
                return new ContainerChest(inventory, invSChest2, player);
            }
            return null;
        }

        @Override
        public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
        {
            InventoryPlayer inventory = player.inventory;
            if (invCChest1 == null)
            {
                invCChest1 = new InventoryBasic("Test Chest 1", true, 27);
            }
            if (invCFurnace == null)
            {
                invCFurnace = new InventoryBasic("Test Furnace", true, 3);
            }
            if (invCChest2 == null)
            {
                invCChest2 = new InventoryBasic("Test Chest 2", true, 27);
            }
            switch (ID)
            {
            case 0:
                return new TestGui(inventory, invCChest1);
            case 1:
                return new TestGui2(inventory, invCFurnace);
            case 2:
                return new GuiChest(inventory, invCChest2);
            }
            return null;
        }

    }

    public static class TabMessageHandler implements IMessageHandler<TabMessageHandler.TabMessage, IMessage>
    {

        @Override
        public IMessage onMessage(TabMessage message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (!player.world.isRemote)
            {
                player.openGui(GuiTabsTest.instance, message.id, player.world, 0, 0, 0);
                return null;
            }
            return null;

        }

        public static class TabMessage implements IMessage
        {

            int id;

            public TabMessage(int id)
            {
                this.id = id;
            }

            public TabMessage()
            {
            }

            @Override
            public void fromBytes(ByteBuf buf)
            {
                id = buf.readInt();
            }

            @Override
            public void toBytes(ByteBuf buf)
            {
                buf.writeInt(id);
            }

        }
    }
}