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

package net.minecraftforge.fml;

import com.google.common.collect.Streams;
import net.minecraftforge.forgespi.language.IModInfo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ModLoadingWarning
{
    /**
     * Mod Info for mod with warning
     */
    private final IModInfo modInfo;
    /**
     * The stage where this warning was encountered
     */
    private final ModLoadingStage warningStage;

    /**
     * I18N message to use for display
     */
    private final String i18nMessage;

    /**
     * Context for message display
     */
    private final List<Object> context;

    public ModLoadingWarning(final IModInfo modInfo, final ModLoadingStage warningStage, final String i18nMessage, Object... context) {
        this.modInfo = modInfo;
        this.warningStage = warningStage;
        this.i18nMessage = i18nMessage;
        this.context = Arrays.asList(context);
    }

    public String formatToString() {
        return ForgeI18n.parseMessage(i18nMessage, Streams.concat(Stream.of(modInfo, warningStage), context.stream()).toArray());
    }
}
