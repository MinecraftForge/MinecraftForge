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

package net.minecraftforge.debug.client.gui;

import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ContainerExtraData;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;

@Mod("extracontainerdatatest")
public class ExtraContainerDataTest {

    @ObjectHolder("extracontainerdatatest:test")
    public static final Block TEST_BLOCK = null;
    
    @ObjectHolder("extracontainerdatatest:test")
    public static final ContainerType<ContainerTest> TEST_CONTAINER = null;

    public ExtraContainerDataTest() {
    	FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
    	FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(ContainerType.class, this::registerContainer);
    	FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Block.class, this::registerBlock);
    	FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerItem);
    }
    
    private void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(TEST_CONTAINER, ScreenTest::new);
    }
    
    public void registerContainer(RegistryEvent.Register<ContainerType<?>> event) {
    	event.getRegistry().register(new ContainerType<ContainerTest>(ContainerTest::new).setRegistryName("extracontainerdatatest:test").setExtraData(TestDataHandler::new));
    }
    
    public void registerBlock(RegistryEvent.Register<Block> event) {
    	event.getRegistry().register(new BlockTest(Block.Properties.create(Material.IRON)).setRegistryName("extracontainerdatatest:test"));
    }
    
    
    public void registerItem(RegistryEvent.Register<Item> event) {
    	event.getRegistry().register(new BlockItem(TEST_BLOCK, new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)).setRegistryName("extracontainerdatatest:test"));
    }
    
    public static class BlockTest extends Block {
    	
    	public BlockTest(Properties properties) {
			super(properties);
		}

		@Override
		public boolean onBlockActivated(BlockState p_220051_1_, World p_220051_2_, BlockPos p_220051_3_, PlayerEntity p_220051_4_, Hand p_220051_5_, BlockRayTraceResult p_220051_6_) {
            if (p_220051_2_.isRemote) {
		         return true;
            } else {
                INamedContainerProvider inamedcontainerprovider = new INamedContainerProvider() {

                    @Override
                    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
                        return new ContainerTest(p_createMenu_1_, p_createMenu_2_);
                    }

					@Override
		            public ITextComponent getDisplayName() {
			            return new StringTextComponent("extracontainerdatatest");
			        } 
		         };
		         
		         p_220051_4_.openContainer(inamedcontainerprovider, new TestDataHandler(p_220051_3_, p_220051_4_.getUniqueID()));
		         return true;
		      }
        }
 
    }
    
    public static class TestDataHandler extends ContainerExtraData {
    	public BlockPos pos;
    	public UUID entityId;
		
		public TestDataHandler() {}
		public TestDataHandler(BlockPos posIn, UUID entityId) {
			this.pos = posIn;
			this.entityId = entityId;
		}
		
		@Override
		public void read(PacketBuffer buf) {
			buf.writeBlockPos(this.pos);
			buf.writeUniqueId(this.entityId);
		}
		
		@Override
		public void write(PacketBuffer buf) {
			this.pos = buf.readBlockPos();
			this.entityId = buf.readUniqueId();
		}
    }
    
    public static class ContainerTest extends Container implements ContainerExtraData.Accept<TestDataHandler>  {
    	public TestDataHandler extraData;
    	
		protected ContainerTest(int windowId, PlayerInventory playerInventory) {
			super(ExtraContainerDataTest.TEST_CONTAINER, windowId);
		}

		@Override
		public boolean canInteractWith(PlayerEntity playerIn) {
			return true;
		}

		@Override
		public void set(TestDataHandler extraData) {
			this.extraData = extraData;
		}
    	
    }
    
    public static class ScreenTest extends ContainerScreen<ContainerTest> {
        
		public ScreenTest(ContainerTest p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
            super(p_i51105_1_, p_i51105_2_, p_i51105_3_);
		}
        
		@Override
		public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
            this.renderBackground();
            super.render(p_render_1_, p_render_2_, p_render_3_);
            this.renderHoveredToolTip(p_render_1_, p_render_2_);
		}
		
		@Override
		protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
			this.font.drawString(this.getContainer().extraData.pos.toString(), 8.0F, 6.0F, -1);
			this.font.drawString(this.getContainer().extraData.entityId.toString(), 8.0F, 25.0F, -1);
		}
		
		@Override
		protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
            
		}
    	
    }

}
