/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import net.minecraft.block.Blocks;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod("containertypetest")
public class ContainerTypeTest
{
    @ObjectHolder("containertypetest:container")
    public static final ContainerType<TestContainer> TYPE = null;
    public class TestContainer extends Container
    {
        protected TestContainer(int windowId, PlayerInventory inv)
        {
            super(TYPE, windowId);
        }

        @Override
        public boolean canInteractWith(PlayerEntity playerIn)
        {
            return true;
        }
    }
    
    public class TestGui extends ContainerScreen<TestContainer>
    {
        public TestGui(TestContainer container, PlayerInventory inv, ITextComponent name)
        {
            super(container, inv, name);
        }

        @Override
        protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
        {
            drawString(this.font, "Hello World!", mouseX, mouseY, -1);
        }
    }

    public ContainerTypeTest()
    {
        FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, this::registerContainers);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.addListener(this::onRightClick);
    }

    private void registerContainers(final RegistryEvent.Register<ContainerType<?>> event)
    {
        event.getRegistry().register(new ContainerType<TestContainer>(TestContainer::new).setRegistryName("container"));
    }
    
    private void setup(FMLClientSetupEvent event)
    {
        ScreenManager.registerFactory(TYPE, TestGui::new);
    }
    
    private void onRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        if (!event.getWorld().isRemote && event.getHand() == Hand.MAIN_HAND)
        {
            if (event.getWorld().getBlockState(event.getPos()).getBlock() == Blocks.SPONGE)
            {
                event.getEntityPlayer().openContainer(new INamedContainerProvider()
                {
                    
                    @Override
                    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_)
                    {
                        return TYPE.func_221506_a(p_createMenu_1_, p_createMenu_2_);
                    }
                    
                    @Override
                    public ITextComponent getDisplayName()
                    {
                        return new StringTextComponent("Test");
                    }
                });
            }
        }
    }
}
