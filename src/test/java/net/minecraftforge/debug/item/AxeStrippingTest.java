package net.minecraftforge.debug.item;

import net.minecraft.block.Blocks;
import net.minecraftforge.common.stripping.AxeStrippingMapRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("axe_stripping_test")
public class AxeStrippingTest {

	public AxeStrippingTest() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
	}
	
	private void commonSetup(final FMLCommonSetupEvent event) {
		AxeStrippingMapRegistry.addStrippingMap(Blocks.ACACIA_FENCE, Blocks.DARK_OAK_FENCE);
		AxeStrippingMapRegistry.addStrippingMap(Blocks.HAY_BLOCK, Blocks.PURPUR_PILLAR);
	}
}
