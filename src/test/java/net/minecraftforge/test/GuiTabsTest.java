package net.minecraftforge.test;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.player.inventory.tabs.GuiTab;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Adds 25 tabs to the player inventory and 13 to a test gui Also checks if item stack icon and ResourceLocation icons are working
 * 
 * @author Ash Indigo
 *
 */
@Mod(name = "GuiTabsTest", modid = "guitabtest", version = "0.0.0.0")
public class GuiTabsTest
{

    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("guitabtest");

    @Instance
    public static GuiTabsTest instance;

    @SidedProxy
    public static CommonProxy proxy;

    @EventHandler
    public void init(FMLPreInitializationEvent event)
    {
        proxy.init();

        CreativeTabs testTab = (new CreativeTabs("test") {
            @SideOnly(Side.CLIENT)
            public ItemStack getTabIconItem()
            {
                return new ItemStack(Item.getItemFromBlock(Blocks.ICE));
            }
        });
    }

    public static class CommonProxy
    {
        public void init()
        {
            NetworkRegistry.INSTANCE.registerGuiHandler(GuiTabsTest.instance, new GuiHandler());
            GuiTabsTest.INSTANCE.registerMessage(TabMessageHandler.class, TabMessageHandler.TabMessage.class, 0, Side.SERVER);
        }
    }

    public static class ServerProxy extends CommonProxy
    {
        public void init()
        {
            super.init();
        }
    }

    public static class ClientProxy extends CommonProxy
    {
        public void init()
        {
            super.init();
            // GuiTab.setDefaultTabForGui(new GuiTab("Tab2T", new ItemStack(Blocks.BRICK_STAIRS), GuiInventory.class) {
            // @Override
            // public void onTabClicked(GuiContainer guiContainer)
            // {
            // super.onTabClicked(guiContainer);
            // Minecraft.getMinecraft().displayGuiScreen(new GuiInventory(Minecraft.getMinecraft().player));
            // }
            // }.setTargetGui(GuiInventory.class).addTo(GuiInventory.class), GuiInventory.class);
            new GuiTab("Tab1", new ItemStack(Blocks.BEDROCK), TestGui.class) {
                @Override
                public void onTabClicked(GuiContainer guiContainer)
                {
                    super.onTabClicked(guiContainer);
                    GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(0));
                }
            }.setTargetGui(TestGui.class).addTo(GuiInventory.class);
            new GuiTab("Tab2", new ResourceLocation("textures/blocks/tnt_side.png"), GuiChest.class) {
                @Override
                public void onTabClicked(GuiContainer guiContainer)
                {
                    super.onTabClicked(guiContainer);
                    GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(1));
                }
            }.setTargetGui(GuiChest.class).addTo(GuiFurnace.class);

            ItemStack is = new ItemStack(Items.APPLE);
            is.addEnchantment(new net.minecraft.enchantment.EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.Type.FALL, null), 1);
            new GuiTab("Tab3", is, TestGui2.class) {
                @Override
                public void onTabClicked(GuiContainer guiContainer)
                {
                    super.onTabClicked(guiContainer);
                    GuiTabsTest.INSTANCE.sendToServer(new TabMessageHandler.TabMessage(2));
                }
            }.setTargetGui(TestGui2.class).addTo(GuiInventory.class);
        }

    }

    public static class TestGui extends GuiChest
    {
        public TestGui(IInventory upperInv, IInventory lowerInv)
        {
            super(upperInv, lowerInv);

        }
    }

    public static class TestGui2 extends TestGui
    {
        public TestGui2(IInventory upperInv, IInventory lowerInv)
        {
            super(upperInv, lowerInv);

        }
    }

    public static class GuiHandler implements IGuiHandler
    {

        IInventory invC;
        IInventory invS;

        @Override
        public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
        {
            if (invS == null)
                invS = new InventoryBasic("Test", true, 27);
            switch (ID)
            {
            default:
                return new ContainerChest(player.inventory, invS, player);
            }
        }

        @Override
        public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
        {
            if (invC == null)
                invC = new InventoryBasic("Test", true, 27);
            switch (ID)
            {
            case 0:
                return new TestGui(player.inventory, invC);
            case 1:
                return new GuiChest(player.inventory, invC);
            case 2:
                return new TestGui2(player.inventory, invC);
            }
            return null;
        }

    }

    public static class TabMessageHandler implements IMessageHandler<TabMessageHandler.TabMessage, IMessage>
    {

        @Override
        public IMessage onMessage(TabMessageHandler.TabMessage message, MessageContext ctx)
        {
            if (ctx.getServerHandler().playerEntity.world.isRemote == false)
            {
                ctx.getServerHandler().playerEntity.openGui(GuiTabsTest.instance, message.id, ctx.getServerHandler().playerEntity.world,
                        ctx.getServerHandler().playerEntity.chunkCoordX, ctx.getServerHandler().playerEntity.chunkCoordY,
                        ctx.getServerHandler().playerEntity.chunkCoordZ);
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
