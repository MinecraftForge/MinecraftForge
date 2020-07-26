package net.minecraftforge.common.stripping;

import java.util.Map.Entry;
import java.util.Optional;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.state.StateHolder;

public class AxeStrippingMap<T extends Block, V extends T> implements IAxeStrippingMap {

	@Nonnull private final T inputBlock;
	@Nonnull private final V outputBlock;
	
	public AxeStrippingMap(T input, V output) {
		this.inputBlock = input;
		this.outputBlock = output;
	}
	
	@Override
	public boolean canStrip(BlockState input) {
		return input.getBlock() == inputBlock;
	}

	@Override
	public BlockState getStrippedState(BlockState input) {
		BlockState output = outputBlock.getDefaultState();
		for(Entry<Property<?>, Comparable<?>> entry : input.getValues().entrySet()) {
			output = withProperty(output, entry.getKey(), entry.getValue().toString());
		}
		return output;
	}
	
	/**
	 * Repurpose of old IStateHolder code. Allows the user to map one BlockState's properties
	 * to another. In the case that the property does not exist, the state does not define the
	 * property value.
	 * 
	 * @param state
	 * 			The current BlockState.
	 * @param property
	 * 			The property to be defined in the BlockState.
	 * @param value
	 * 			A string of the property value.
	 * @return The resultant BlockState with the set property and value if defined in the current
	 * 			StateContainer.
	 */
	private static <S extends StateHolder<?, S>, T extends Comparable<T>> S withProperty(S state, Property<T> property, String value) {
		Optional<T> opt = property.parseValue(value);
		if(opt.isPresent()) {
			return (S) (state.with(property, (T)(opt.get())));
		} else {
			return state;
		}
	}
}
