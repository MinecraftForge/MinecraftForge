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

package net.minecraftforge.common;

import com.google.common.collect.Sets;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.common.extensions.IForgeItem;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToolActions
{
    /**
     *  Exposed by axes to allow querying tool behaviours
     */
    public static final ToolAction AXE_DIG = ToolAction.get("axe_dig");

    /**
     *  Exposed by pickaxes to allow querying tool behaviours
     */
    public static final ToolAction PICKAXE_DIG = ToolAction.get("pickaxe_dig");

    /**
     *  Exposed by shovels to allow querying tool behaviours
     */
    public static final ToolAction SHOVEL_DIG = ToolAction.get("shovel_dig");

    /**
     *  Exposed by shovels to allow querying tool behaviours
     */
    public static final ToolAction HOE_DIG = ToolAction.get("hoe_dig");

    /**
     *  Exposed by swords to allow querying tool behaviours
     */
    public static final ToolAction SWORD_DIG = ToolAction.get("sword_dig");

    /**
     *  Exposed by shears to allow querying tool behaviours
     */
    public static final ToolAction SHEARS_DIG = ToolAction.get("shears_dig");

    /**
     *  Passed onto {@link IForgeBlock#getToolModifiedState} when an axe wants to strip a log
     */
    public static final ToolAction AXE_STRIP = ToolAction.get("axe_strip");

    /**
     *  Passed onto {@link IForgeBlock#getToolModifiedState} when an axe wants to scrape oxidization off copper
     */
    public static final ToolAction AXE_SCRAPE = ToolAction.get("axe_scrape");

    /**
     *  Passed onto {@link IForgeBlock#getToolModifiedState} when an axe wants to remove wax out of copper
     */
    public static final ToolAction AXE_WAX_OFF = ToolAction.get("axe_wax_off");

    /**
     *  Passed onto {@link IForgeBlock#getToolModifiedState} when a shovel wants to turn dirt into path
     */
    public static final ToolAction SHOVEL_FLATTEN = ToolAction.get("shovel_flatten");

    /**
     *  Used during player attack to figure out if a sweep attack should be performed
     *  
     *  @see {@link IForgeItem#getSweepHitBox}
     */
    public static final ToolAction SWORD_SWEEP = ToolAction.get("sword_sweep");

    /**
     *  This action is exposed by shears and corresponds to a harvest action that is triggered with a right click on a block that supports such behaviour.
     *  Example: Right click with shears on a beehive with honey level 5 to harvest it
     */
    public static final ToolAction SHEARS_HARVEST = ToolAction.get("shears_harvest");

    /**
     *  This action is exposed by shears and corresponds to a carve action that is triggered with a right click on a block that supports such behaviour.
     *  Example: Right click with shears o a pumpkin to carve it
     */
    public static final ToolAction SHEARS_CARVE = ToolAction.get("shears_carve");

    /**
     *  This action is exposed by shears and corresponds to a disarm action that is triggered by breaking a block that supports such behaviour.
     *  Example: Breaking a trip wire with shears to disarm it.
     */
    public static final ToolAction SHEARS_DISARM = ToolAction.get("shears_disarm");

    ///**
    // *  Passed onto {@link IForgeBlock#getToolModifiedState} when a hoe wants to turn dirt into soil
    // */
    // TODO: public static final ToolAction HOE_TILL = ToolAction.get("till");

    /**
     * A tool action corresponding to the 'block' action of shields.
     */
    public static final ToolAction SHIELD_BLOCK = ToolAction.get("shield_block");

    // Default actions supported by each tool type
    public static final Set<ToolAction> DEFAULT_AXE_ACTIONS = of(AXE_DIG, AXE_STRIP, AXE_SCRAPE, AXE_WAX_OFF);
    public static final Set<ToolAction> DEFAULT_HOE_ACTIONS = of(HOE_DIG /* TODO: , HOE_TILL */);
    public static final Set<ToolAction> DEFAULT_SHOVEL_ACTIONS = of(SHOVEL_DIG, SHOVEL_FLATTEN);
    public static final Set<ToolAction> DEFAULT_PICKAXE_ACTIONS = of(PICKAXE_DIG);
    public static final Set<ToolAction> DEFAULT_SWORD_ACTIONS = of(SWORD_DIG, SWORD_SWEEP);
    public static final Set<ToolAction> DEFAULT_SHEARS_ACTIONS = of(SHEARS_DIG, SHEARS_HARVEST, SHEARS_CARVE, SHEARS_DISARM);
    public static final Set<ToolAction> DEFAULT_SHIELD_ACTIONS = of(SHIELD_BLOCK);

    private static Set<ToolAction> of(ToolAction... actions) {
        return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
    }
}
