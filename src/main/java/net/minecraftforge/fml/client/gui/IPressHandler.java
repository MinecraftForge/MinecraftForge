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

package net.minecraftforge.fml.client.gui;

import net.minecraft.client.gui.widget.button.Button;

import javax.annotation.ParametersAreNonnullByDefault;

/**
 * Sub-interface with generic parameter to allow using correct class in lambda
 */
public interface IPressHandler<W extends Button> extends Button.IPressable {

    @ParametersAreNonnullByDefault
    @SuppressWarnings("unchecked")
    @Override
    default void onPress(Button p_onPress_1_) {
        onPress2((W) p_onPress_1_);
    }

    /**
     * @param button {@link Button} instance
     */
    void onPress2(W button);
}
