package net.minecraftforge.fml.client.config.entry.widget;

/**
 * Implement this on your enum to have special coloring applied to your enum when displayed in the Config GUI.
 */
public interface IDisplayColorableEnum {

	/**
	 * @return The display color for the specific enum
	 */
	int getDisplayColor();

}
