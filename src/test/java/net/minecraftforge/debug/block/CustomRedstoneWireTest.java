package net.minecraftforge.debug.block;

import net.minecraft.block.*;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(CustomRedstoneWireTest.MODID)
public class CustomRedstoneWireTest {

	static final String MODID = "custom_redstone_wire_test";
	static final String BLOCK_ID = "custom_redstone_wire";

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

	public static final RegistryObject<RedstoneWireBlock> CUSTOM_REDSTONE_WIRE = BLOCKS.register(BLOCK_ID, () -> new RedstoneWireBlock(AbstractBlock.Properties.from(Blocks.REDSTONE_WIRE)) {
		@Override
		public boolean canConnectToOther(BlockState thisState, BlockState otherState) {
			return true;
		}
	});

	static {
		ITEMS.register(BLOCK_ID, () -> new BlockItem(CUSTOM_REDSTONE_WIRE.get(), new Item.Properties()));
	}

	public CustomRedstoneWireTest() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		bus.addListener(this::clientSetup);

		BLOCKS.register(bus);
		ITEMS.register(bus);
	}

	private void clientSetup(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(CUSTOM_REDSTONE_WIRE.get(), RenderType.getCutout());
	}


}
