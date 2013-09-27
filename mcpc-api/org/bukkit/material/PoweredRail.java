package org.bukkit.material;

import org.bukkit.Material;

/**
 * Represents a powered rail
 */
public class PoweredRail extends ExtendedRails implements Redstone {
    public PoweredRail() {
        super(Material.POWERED_RAIL);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public PoweredRail(final int type) {
        super(type);
    }

    public PoweredRail(final Material type) {
        super(type);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public PoweredRail(final int type, final byte data) {
        super(type, data);
    }

    /**
     *
     * @deprecated Magic value
     */
    @Deprecated
    public PoweredRail(final Material type, final byte data) {
        super(type, data);
    }

    public boolean isPowered() {
        return (getData() & 0x8) == 0x8;
    }

    /**
     * Set whether this PoweredRail should be powered or not.
     *
     * @param isPowered whether or not the rail is powered
     */
    public void setPowered(boolean isPowered) {
        setData((byte) (isPowered ? (getData() | 0x8) : (getData() & ~0x8)));
    }

    @Override
    public PoweredRail clone() {
        return (PoweredRail) super.clone();
    }
}
