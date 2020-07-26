package net.minecraftforge.common.stripping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public class AxeStrippingMapRegistry {

	private static List<IAxeStrippingMap> stripping_maps = new ArrayList<>();
	
	static
	{
		addStrippingMap(new VanillaAxeStrippingMap());
	}
	
	/**
	 * Adds a stripping map to the registry. To mimic the original behavior of
	 * the vanilla stripping map, the input class must be or extend the output class.
	 * 
	 * @param input
	 * 			The Block that can be stripped.
	 * @param output
	 * 			The resulting stripped Block.
	 * @return True if the stripping map was added.
	 */
	public static <T extends Block, V extends T> boolean addStrippingMap(T input, V output) {
		return addStrippingMap(new AxeStrippingMap<>(input, output));
	}
	
	/**
	 * Adds a stripping map to the registry.
	 * 
	 * @param map
	 * 			The stripping map.
	 * @return True if the stripping map was added.
	 */
	public static boolean addStrippingMap(IAxeStrippingMap map) {
		return stripping_maps.add(map);
	}
	
	/**
     * Returns the stripping map associated with the provided BlockState if
     * available. Null otherwise.
     * 
     * @param input
     * 			The BlockState that can be stripped.
     * @return The stripping map associated with Block.
     */
	@Nullable
	public static IAxeStrippingMap getStrippingMap(BlockState state)
	{
		for(IAxeStrippingMap map : stripping_maps)
		{
			if(map.canStrip(state))
			{
				return map;
			}
		}
		return null;
	}
	
	/**
     * @return An unmodifiable list containing all of the stripping maps in the registry.
     */
	public static List<IAxeStrippingMap> getStrippingMaps()
	{
		return Collections.unmodifiableList(stripping_maps);
	}
}
