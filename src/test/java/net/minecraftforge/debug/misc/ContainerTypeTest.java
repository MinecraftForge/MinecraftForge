/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.debug.misc;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.ObjectHolder;

@Mod("containertypetest")
public class ContainerTypeTest
{
    @ObjectHolder("containertypetest:container")
    public static final MenuType<TestContainer> TYPE = null;
    public class TestContainer extends AbstractContainerMenu
    {
        private final String text;
        
        protected TestContainer(int windowId, Inventory playerInv, FriendlyByteBuf extraData)
        {
            this(windowId, new SimpleContainer(9), extraData.readUtf(128));
        }
        
        public TestContainer(int windowId, SimpleContainer inv, String text)
        {
            super(TYPE, windowId);
            this.text = text;
            for (int i = 0; i < 9; i++)
            {
                this.addSlot(new Slot(inv, i, (i % 3) * 18, (i / 3) * 18));
            }
        }

        @Override
        public boolean stillValid(Player playerIn)
        {
            return true;
        }
    }
    
    public class TestGui extends AbstractContainerScreen<TestContainer>
    {
        public TestGui(TestContainer container, Inventory inv, Component name)
        {
            super(container, inv, name);
        }

        @Override
        protected void renderBg(PoseStack poseStack, float partialTick, int mouseX, int mouseY)
        {
            drawString(poseStack, this.font, getMenu().text, mouseX, mouseY, -1);
        }
    }

    public ContainerTypeTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(MenuType.class, this::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::onRightClick);
    }

    private void registerContainers(final RegistryEvent.Register<MenuType<?>> event)
    {
        event.getRegistry().register(IForgeMenuType.create(TestContainer::new).setRegistryName("container"));
    }
    
    private void setup(FMLClientSetupEvent event)
    {
        MenuScreens.register(TYPE, TestGui::new);
    }
    
    private void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if (!event.getWorld().isClientSide && event.getHand() == InteractionHand.MAIN_HAND)
        {
            if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.SPONGE)
            {
                String text = "Hello World!";
                NetworkHooks.openGui((ServerPlayer) event.getPlayer(), new MenuProvider()
                {
                    @Override
                    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_)
                    {
                        SimpleContainer inv = new SimpleContainer(9);
                        for (int i = 0; i < inv.getContainerSize(); i++)
                        {
                            inv.setItem(i, new ItemStack(Items.DIAMOND));
                        }
                        return new TestContainer(p_createMenu_1_, inv, text);
                    }
                    
                    @Override
                    public Component getDisplayName()
                    {
                        return new TextComponent("Test");
                    }
                }, extraData -> {
                    extraData.writeUtf(text);
                });
            }
        }
    }
}
