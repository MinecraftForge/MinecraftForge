/*
 * Minecraft Forge
 * Marcel Sondaar
 * Copyright (c) 2017.
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

package net.minecraftforge.event;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.ImmutableList;
import net.minecraft.advancements.Advancement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.IContextSetter;


/**
 * Programmatic installation of advancements.
 */
public class AdvancementRegisterEvent extends Event implements IContextSetter
{
    public final Map<ResourceLocation, Advancement.Builder> advancements;

    public AdvancementRegisterEvent(Map<ResourceLocation, Advancement.Builder> map) {
        super();
	this.advancements = map;
    }
}
