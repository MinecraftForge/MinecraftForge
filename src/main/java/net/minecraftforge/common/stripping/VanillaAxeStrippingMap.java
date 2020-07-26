package net.minecraftforge.common.stripping;

import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.item.AxeItem;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

/**
 * Used in AxeStrippingMapRegistry to maintain the vanilla behavior.
 * 
 * The code simply copies AxeItem#BLOCK_STRIPPING_MAP and checks whether or not 
 * the BlockState can be stripped.
 */
public class VanillaAxeStrippingMap implements IAxeStrippingMap {

	private static final Map<Block, Block> BLOCK_STRIPPING_MAP = ObfuscationReflectionHelper.getPrivateValue(AxeItem.class, null, "field_203176_a");
	
	@Override
	public boolean canStrip(BlockState input) {
		return BLOCK_STRIPPING_MAP.containsKey(input.getBlock());
	}

	@Override
	public BlockState getStrippedState(BlockState input) {
		return BLOCK_STRIPPING_MAP.get(input.getBlock()).getDefaultState().with(RotatedPillarBlock.AXIS, input.get(RotatedPillarBlock.AXIS));
	}
}
