/*
 * Minecraft Forge
 * Copyright (c) 2016-2018.
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

package net.minecraftforge.fml.client.event;

import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.Event.HasResult;

import javax.annotation.Nullable;

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
public class ConfigChangedEvent extends net.minecraftforge.eventbus.api.Event
{
    private final String  modID;
    private final boolean isWorldRunning;
    private final boolean requiresMcRestart;
    @Nullable
    private final String configID;

    public ConfigChangedEvent(String modID, @Nullable String configID, boolean isWorldRunning, boolean requiresMcRestart)
    {
        this.modID = modID;
        this.configID = configID;
        this.isWorldRunning = isWorldRunning;
        this.requiresMcRestart = requiresMcRestart;
    }

    /**
     * The Mod ID of the mod whose configuration just changed.
     */
    public String getModID()
    {
        return modID;
    }

    /**
     * Whether or not a world is currently running.
     */
    public boolean isWorldRunning()
    {
        return isWorldRunning;
    }

    /**
     * Will be set to true if any elements were changed that require a restart of Minecraft.
     */
    public boolean isRequiresMcRestart()
    {
        return requiresMcRestart;
    }

    /**
     * A String identifier for this ConfigChangedEvent.
     */
    @Nullable
    public String getConfigID()
    {
        return configID;
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
        public OnConfigChangedEvent(String modID, @Nullable String configID, boolean isWorldRunning, boolean requiresMcRestart)
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
        public PostConfigChangedEvent(String modID, @Nullable String configID, boolean isWorldRunning, boolean requiresMcRestart)
        {
            super(modID, configID, isWorldRunning, requiresMcRestart);
        }
    }
}
