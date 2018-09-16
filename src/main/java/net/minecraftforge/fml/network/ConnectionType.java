/*
 * Minecraft Forge
 * Copyright (c) 2018.
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

import java.util.Arrays;
import java.util.Objects;

public enum ConnectionType
{
    MODDED(NetworkHooks.NETVERSION), VANILLA(NetworkHooks.NOVERSION);

    private final String versionString;

    ConnectionType(String versionString)
    {
        this.versionString = versionString;
    }

    public static ConnectionType forVersionFlag(String vers)
    {
        return Arrays.stream(values()).filter(ct->Objects.equals(ct.versionString, vers)).
                findFirst().orElse(ConnectionType.VANILLA);
    }
}
