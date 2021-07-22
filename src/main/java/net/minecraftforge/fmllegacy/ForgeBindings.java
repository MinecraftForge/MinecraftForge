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

package net.minecraftforge.fmllegacy;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.I18NParser;
import net.minecraftforge.fml.IBindingsProvider;
import net.minecraftforge.fml.config.IConfigEvent;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ForgeBindings implements IBindingsProvider {
    @Override
    public Supplier<IEventBus> getForgeBusSupplier() {
        return ()-> MinecraftForge.EVENT_BUS;
    }

    @Override
    public Supplier<I18NParser> getMessageParser() {
        return ()->new I18NParser() {
            @Override
            public String parseMessage(final String i18nMessage, final Object... args) {
                return ForgeI18n.parseMessage(i18nMessage, args);
            }

            @Override
            public String stripControlCodes(final String toStrip) {
                return ForgeI18n.stripControlCodes(toStrip);
            }
        };
    }

    @Override
    public Supplier<IConfigEvent.ConfigConfig> getConfigConfiguration() {
        return ()->new IConfigEvent.ConfigConfig(ModConfigEvent.Loading::new, ModConfigEvent.Reloading::new);
    }
}
