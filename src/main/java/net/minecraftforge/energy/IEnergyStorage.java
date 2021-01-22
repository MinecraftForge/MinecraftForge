/*
 * Minecraft Forge
 * Copyright (c) 2016-2021.
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

package net.minecraftforge.energy;

/**
 * An energy storage is the unit of interaction with Energy inventories.
 * <p>
 * A reference implementation can be found at {@link EnergyStorage}.
 *
 * Derived from the Redstone Flux power system designed by King Lemming and originally utilized in Thermal Expansion and related mods.
 * Created with consent and permission of King Lemming and Team CoFH. Released with permission under LGPL 2.1 when bundled with Forge.
 *
 */
public interface IEnergyStorage
{
    /**
    * Adds energy to the storage. Returns quantity of energy that was accepted.
    *
    * @param maxReceive
    *            Maximum amount of energy to be inserted.
    * @param simulate
    *            If TRUE, the insertion will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) accepted by the storage.
    */
    int receiveEnergy(int maxReceive, boolean simulate);

    /**
    * Removes energy from the storage. Returns quantity of energy that was removed.
    *
    * @param maxExtract
    *            Maximum amount of energy to be extracted.
    * @param simulate
    *            If TRUE, the extraction will only be simulated.
    * @return Amount of energy that was (or would have been, if simulated) extracted from the storage.
    */
    int extractEnergy(int maxExtract, boolean simulate);

    /**
    * Returns the amount of energy currently stored.
    */
    int getEnergyStored();

    /**
    * Returns the maximum amount of energy that can be stored.
    */
    int getMaxEnergyStored();

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    boolean canExtract();

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    boolean canReceive();

}
