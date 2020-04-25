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

package net.minecraftforge.fml.network;

import java.util.function.Function;

public enum ConnectionType
{
    MODDED(s->Integer.valueOf(s.substring(FMLNetworkConstants.FMLNETMARKER.length()))), VANILLA(s->0);

    private final Function<String, Integer> versionExtractor;

    ConnectionType(Function<String, Integer> versionExtractor) {
        this.versionExtractor = versionExtractor;
    }

    public static ConnectionType forVersionFlag(String vers)
    {
        return vers.startsWith(FMLNetworkConstants.FMLNETMARKER) ? MODDED : VANILLA;
    }

    public int getFMLVersionNumber(final String fmlVersion) {
        return versionExtractor.apply(fmlVersion);
    }
}
