/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
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

import com.mojang.datafixers.util.Pair;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

/**
 * This event is called after all village
 * {@link net.minecraft.world.gen.feature.jigsaw.JigsawPattern} and {@link net.minecraft.world.gen.feature.jigsaw.JigsawPiece} are initialized.
 *
 * If you want to add custom JigsawPieces to an existing village JigsawPattern, catch this event and
 * get {@link net.minecraft.world.gen.feature.jigsaw.JigsawPatternRegistry#get(ResourceLocation)} from {@link net.minecraft.world.gen.feature.jigsaw.JigsawManager#field_214891_a}
 * and call {@link net.minecraft.world.gen.feature.jigsaw.JigsawPattern#addBuilding(Pair)
 */
public class VillageEvent extends Event {

}
