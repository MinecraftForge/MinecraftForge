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

package net.minecraftforge.client.event.sound;

import net.minecraftforge.eventbus.api.Event;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SoundManager;

public class SoundEvent extends net.minecraftforge.eventbus.api.Event
{
    private final SoundManager manager;
    public SoundEvent(SoundManager manager)
    {
        this.manager = manager;
    }

    public SoundManager getManager()
    {
        return manager;
    }

    public static class SoundSourceEvent extends SoundEvent
    {
        private final ISound sound;
        private final String uuid;
        private final String name;

        public SoundSourceEvent(SoundManager manager, ISound sound, String uuid)
        {
            super(manager);
            this.name = sound.getSoundLocation().getPath();
            this.sound = sound;
            this.uuid = uuid;
        }

        public ISound getSound()
        {
            return sound;
        }

        public String getUuid()
        {
            return uuid;
        }

        public String getName()
        {
            return name;
        }
    }
}
