package org.bukkit.material;

import org.bukkit.DyeColor;
import org.bukkit.Material;

/**
 * Represents dye
 */
public class Dye extends MaterialData implements Colorable {
    public Dye() {
        super(Material.INK_SACK);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Dye(final int type) {
        super(type);
    }

    public Dye(final Material type) {
        super(type);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Dye(final int type, final byte data) {
        super(type, data);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public Dye(final Material type, final byte data) {
        super(type, data);
    }

    /**
     * Gets the current color of this dye
     *
     * @return DyeColor of this dye
     */
    public DyeColor getColor() {
        return DyeColor.getByDyeData(getData());
    }

    /**
     * Sets the color of this dye
     *
     * @param color New color of this dye
     */
    public void setColor(DyeColor color) {
        setData(color.getDyeData());
    }

    @Override
    public String toString() {
        return getColor() + " DYE(" + getData() + ")";
    }

    @Override
    public Dye clone() {
        return (Dye) super.clone();
    }
}
