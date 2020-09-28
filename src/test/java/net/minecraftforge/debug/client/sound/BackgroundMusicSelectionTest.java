/*
 * Minecraft Forge
 * Copyright (c) 2016-2020.
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

package net.minecraftforge.debug.client.sound;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.audio.BackgroundMusicTracks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.sound.BackgroundMusicSelectionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("background_music_selection_test")
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class BackgroundMusicSelectionTest
{
    static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onMusicSelection(final BackgroundMusicSelectionEvent event)
    {
        //Checks values to make sure the selection event is working
        LOGGER.debug("Current Sound: {}, Selector: {}", event.getCurrentMusic(), event.getDefaultSelector());
        //Sets the music to always play the credits sequence
        event.setBackgroundMusicSelector(BackgroundMusicTracks.field_232672_c_);
    }
}
