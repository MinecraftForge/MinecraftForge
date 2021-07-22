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

package net.minecraftforge.fmlclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Optional;
import java.util.function.BiFunction;

public class ConfigGuiHandler
{
    public record ConfigGuiFactory(BiFunction<Minecraft, Screen, Screen> screenFunction) implements IExtensionPoint<ConfigGuiFactory> {}
    public static Optional<BiFunction<Minecraft, Screen, Screen>> getGuiFactoryFor(IModInfo selectedMod)
    {
        return ModList.get().getModContainerById(selectedMod.getModId()).
                flatMap(mc -> mc.getCustomExtension(ConfigGuiFactory.class).map(ConfigGuiFactory::screenFunction));
    }
}
