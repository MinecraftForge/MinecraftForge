/*
 * Forge Mod Loader
 * Copyright (c) 2012-2014 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors (this class):
 *     bspkrs - implementation
 */

package net.minecraftforge.fml.client.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.Event.HasResult;

/**
 * These events are posted from the GuiConfig screen when the done button is pressed. The events are only posted
 * if the parent screen is not an instance of GuiConfig or if the configID field has been set for
 * the GuiConfig screen. 
 * 
 * Listeners for this event should use OnConfigChanged or PostConfigChanged and check for a specific mod ID.
 * For best results the listener should refresh any objects/fields that are set based on the mod's config
 * and should serialize the modified config.
 * 
 * @author bspkrs
 */
@HasResult
public class ConfigChangedEvent extends Event
{
    /**
     * The Mod ID of the mod whose configuration just changed.
     */
    public final String  modID;
    /**
     * Whether or not a world is currently running.
     */
    public final boolean isWorldRunning;
    /**
     * Will be set to true if any elements were changed that require a restart of Minecraft.
     */
    public final boolean requiresMcRestart;
    /**
     * A String identifier for this ConfigChangedEvent.
     */
    public final String configID;
    
    public ConfigChangedEvent(String modID, String configID, boolean isWorldRunning, boolean requiresMcRestart)
    {
        this.modID = modID;
        this.configID = configID;
        this.isWorldRunning = isWorldRunning;
        this.requiresMcRestart = requiresMcRestart;
    }
    
    /**
     * This event is intended to be consumed by the mod whose config has been changed. It fires when the Done button
     * has been clicked on a GuiConfig screen and the following conditions are met:<br/>
     * - at least one config element has been changed<br/>
     * - one of these 2 conditions are met:<br/>
     *      1) the parent screen is null or is not an instance of GuiConfig<br/>
     *      2) the configID field has been set to a non-null value for the GuiConfig screen<br/><br/>
     * Modders should check the modID field of the event to ensure they are only acting on their own config screen's event!
     */
    public static class OnConfigChangedEvent extends ConfigChangedEvent
    {
        public OnConfigChangedEvent(String modID, String configID, boolean isWorldRunning, boolean requiresMcRestart)
        {
            super(modID, configID, isWorldRunning, requiresMcRestart);
        }
    }
    
    /**
     * This event is provided for mods to consume if they want to be able to check if other mods' configs have been changed.
     * This event only fires if the OnConfigChangedEvent result is not DENY.
     */
    public static class PostConfigChangedEvent extends ConfigChangedEvent
    {
        public PostConfigChangedEvent(String modID, String configID, boolean isWorldRunning, boolean requiresMcRestart)
        {
            super(modID, configID, isWorldRunning, requiresMcRestart);
        }
    }
}